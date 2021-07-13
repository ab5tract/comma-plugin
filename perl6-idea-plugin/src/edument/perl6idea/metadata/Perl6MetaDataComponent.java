package edument.perl6idea.metadata;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.module.Perl6ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class Perl6MetaDataComponent {
    public static final String META6_JSON_NAME = "META6.json";
    public static final String META_OBSOLETE_NAME = "META.info";
    @Nullable
    private Module myModule = null;
    @Nullable
    private VirtualFile myMetaFile = null;
    @Nullable
    private JSONObject myMeta = null;

    public Perl6MetaDataComponent(Module module) {
        String name = module.getModuleTypeName();

        if (name == null || !name.equals(Perl6ModuleType.getInstance().getId()))
            return;

        this.myModule = module;

        // VFS events WILL NOT be passed to the module message bus,
        // so connect to project one instead
        this.myModule.getProject().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event.getFile() == null) continue;
                    String fileName = event.getFile().getName();
                    if (!event.isFromSave() ||
                        !(fileName.equals(META6_JSON_NAME) || fileName.equals(META_OBSOLETE_NAME))) return;
                    // Above we did a simple check to do a fast filtering of completely unrelated files,
                    // but as we work with project-level bus, do the heavy check to make extra sure
                    // it is the meta file of a module we are working with
                    if (!event.getFile().equals(myMetaFile)) continue;
                    myMeta = checkMetaSanity();
                    saveFile();
                    if (myMeta != null) {
                        Perl6ProjectModelSync.syncExternalLibraries(Perl6MetaDataComponent.this.myModule, getAllDependencies());
                    }
                }
            }
        });

        VirtualFile metaParent = calculateMetaParent();
        if (metaParent == null) return;

        VirtualFile metaFile = metaParent.findChild(META6_JSON_NAME);
        // Try to search by obsolete 'META.info' name and warn about it if present
        if (metaFile == null) {
            metaFile = checkOldMetaFile(metaParent);

            // If everything fails, notify about META absence
            // and suggest to stub it
            if (metaFile == null) {
                notifyMissingMETA();
                return;
            }
        }
        myMetaFile = metaFile;
        myMeta = checkMetaSanity();

        // Load dependencies
        if (myMeta != null) {
            Perl6ProjectModelSync.syncExternalLibraries(this.myModule, getAllDependencies());
        }
    }

    @Nullable
    public Module getModule() {
        return myModule;
    }

    private VirtualFile checkOldMetaFile(VirtualFile metaParent) {
        VirtualFile metaFile;
        metaFile = metaParent.findChild(META_OBSOLETE_NAME);
        if (metaFile != null) {
            notifyMetaIssue(
                String.format("Obsolete '%s' file name is used instead of '%s'", META_OBSOLETE_NAME, META6_JSON_NAME),
                NotificationType.ERROR,
                new AnAction(String.format("Rename to %s", META6_JSON_NAME)) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent event) {
                        ApplicationManager.getApplication().invokeLater(
                            () -> WriteAction.run(() -> {
                                try {
                                    metaFile.rename(this, META6_JSON_NAME);
                                }
                                catch (IOException ex) {
                                    Notifications.Bus.notify(
                                        new Notification(
                                            "raku.meta.errors", "Raku META error",
                                            "Could not rename META file: " + ex.getMessage(),
                                            NotificationType.ERROR));
                                }
                            })
                        );
                    }
                });
        }
        return metaFile;
    }

    private JSONObject checkMetaSanity() {
        JSONObject meta = null;
        if (myMetaFile != null && myMetaFile.exists()) {
            try {
                String content = new String(myMetaFile.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET);
                meta = new JSONObject(content);
                checkMetaSanity(meta);
            }
            catch (Perl6MetaException e) {
                notifyMetaIssue(e.getMessage(), NotificationType.ERROR, e.myFix);
            }
            catch (IOException | JSONException e) {
                notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
            }
        }
        return meta;
    }

    public List<String> getResources() {
        if (myMeta == null || !myMeta.has("resources"))
            return new ArrayList<>();
        JSONArray resources = myMeta.getJSONArray("resources");
        return ContainerUtil.map(resources.toList(), r -> r instanceof String ? (String)r : null);
    }

    public void removeResource(String name) {
        if (myMeta == null)
            return;
        JSONArray resources = myMeta.getJSONArray("resources");
        if (resources != null) {
            List<Object> resourceList = resources.toList();
            for (int i = 0, size = resourceList.size(); i < size; i++) {
                Object resource = resourceList.get(i);
                if (resource instanceof String && resource.equals(name)) {
                    resources.remove(i);
                    myMeta.put("resources", resources);
                    saveFile();
                    break;
                }
            }
        }
    }

    public void addResource(String newName) {
        if (myMeta == null)
            return;
        if (!myMeta.has("resources"))
            myMeta.put("resources", new JSONArray());
        JSONArray resources = myMeta.getJSONArray("resources");
        resources.put(newName);
        myMeta.put("resources", resources);
        saveFile();
    }

    /* Enforces number of rules for meta to check on start and every saving */
    private static void checkMetaSanity(JSONObject meta) throws Perl6MetaException {
        checkParameter(meta, "name", v -> v instanceof String, "string");
        checkParameter(meta, "description", v -> v instanceof String, "string");
        checkParameter(meta, "version", v -> v instanceof String, "string");
        checkParameter(meta, "auth", v -> v instanceof String, "string");
        checkParameter(meta, "license", v -> v instanceof String, "string");

        checkParameter(meta, "depends", v ->
            v instanceof JSONArray && ((JSONArray)v).toList().stream().allMatch(iv -> iv instanceof String), "string array");

        checkParameter(meta, "provides", v ->
            v instanceof JSONObject && ((JSONObject)v).toMap().values().stream().allMatch(iv -> iv instanceof String), "provides object");
    }

    private static void checkParameter(JSONObject meta, String name,
                                       Function<Object, Boolean> check, String className) throws Perl6MetaException {
        if (!meta.has(name) || !check.fun(meta.get(name))) {
            throw new Perl6MetaException(
                meta.has(name) ?
                String.format("Value of '%s' field is not a %s", name, className) :
                String.format("'%s' field is not present", name));
        }
    }

    public void triggerMetaBuild() {
        VirtualFile metaParent = calculateMetaParent();
        if (metaParent == null) return;
        VirtualFile metaFile = metaParent.findChild(META6_JSON_NAME);
        if (metaFile != null) {
            triggerMetaBuild(metaFile);
        }
    }

    public void triggerMetaBuild(@NotNull VirtualFile metaFile) {
        myMetaFile = metaFile;
        myMeta = checkMetaSanity();
        if (myMeta != null) {
            Perl6ProjectModelSync.syncExternalLibraries(myModule, getAllDependencies());
        }
    }

    public boolean isMetaDataExist() {
        return myMeta != null;
    }

    public void addDepends(String name) {
        addDependsInternal("depends", name);
    }

    public void addTestDepends(String name) {
        addDependsInternal("test-depends", name);
    }

    public void addBuildDepends(String name) {
        addDependsInternal("build-depends", name);
    }

    private void addDependsInternal(String key, String name) {
        if (myMeta == null || !myMeta.has(key)) return;
        Object depends = myMeta.has(key) ? myMeta.get(key) : new JSONArray();
        if (!(depends instanceof JSONArray)) return;
        JSONArray dependsArray = (JSONArray)depends;
        if (dependsArray.toList().contains(name))
            return;
        dependsArray.put(name);
        myMeta.put(key, dependsArray);
        saveFile();
    }

    public Set<String> getAllDependencies() {
        Set<String> metaDependencies = new HashSet<>();
        metaDependencies.addAll(getDepends(false));
        metaDependencies.addAll(getTestDepends(false));
        metaDependencies.addAll(getBuildDepends(false));
        return metaDependencies;
    }

    public List<String> getDepends(boolean normalize) {
        return getDependsInternal("depends", normalize);
    }


    public List<String> getTestDepends(boolean normalize) {
        return getDependsInternal("test-depends", normalize);
    }

    public List<String> getBuildDepends(boolean normalize) {
        return getDependsInternal("build-depends", normalize);
    }

    private List<String> getDependsInternal(String key, boolean normalize) {
        if (myMeta == null) return new ArrayList<>();
        if (!myMeta.has(key)) return new ArrayList<>();
        Object depends = myMeta.get(key);
        if (!(depends instanceof JSONArray)) return new ArrayList<>();
        JSONArray dependsArray = (JSONArray)depends;
        List<String> result = new ArrayList<>();
        if (normalize)
            dependsArray.toList().forEach(o -> result.add(normalizeDepends((String)o)));
        else
            dependsArray.toList().forEach(o -> result.add((String)o));
        return result;
    }

    public void setDepends(List<String> depends) {
        setDependsInternal("depends", depends);
    }

    public void setTestDepends(List<String> testDepends) {
        setDependsInternal("test-depends", testDepends);
    }

    public void setBuildDepends(List<String> buildDepends) {
        setDependsInternal("build-depends", buildDepends);
    }

    private void setDependsInternal(String key, List<String> buildDepends) {
        if (myMeta == null) return;
        myMeta.put(key, new JSONArray(buildDepends));
        saveFile();
    }

    @Nullable
    public Map<String, Object> getProvidedMap() {
        if (myMeta == null || !myMeta.has("provides"))
            return null;

        Object provides = myMeta.get("provides");
        if (provides instanceof JSONObject)
            return ((JSONObject)provides).toMap();
        return null;
    }

    public Collection<String> getProvidedNames() {
        Map<String, Object> providedSet = getProvidedMap();
        if (providedSet == null)
            return new ArrayList<>();
        return providedSet.keySet();
    }

    private static String normalizeDepends(String name) {
        String[] parts = name.split("::");
        List<String> symbolParts = new ArrayList<>();
        for (String part : parts) {
            int index = part.indexOf(':');
            symbolParts.add(index == -1 ? part : part.substring(0, index));
            if (index != -1) break;
        }
        return String.join("::", symbolParts);
    }

    public void createStubMetaFile(String moduleName, VirtualFile firstRoot, boolean shouldOpenEditor) throws IOException {
        if (myModule == null)
            return;
        if (firstRoot == null)
            firstRoot = calculateMetaParent();
        if (firstRoot == null) {
            ContentEntry[] entries = ModuleRootManager.getInstance(myModule).getContentEntries();
            VirtualFile file = FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                myModule.getProject(), entries.length == 1 && entries[0].getFile() != null ? entries[0].getFile() : null);
            if (file == null) {
                notifyMetaIssue("Directory was not selected, meta file creation is canceled", NotificationType.INFORMATION);
                return;
            }
            else {
                firstRoot = file;
            }
        }

        AtomicReference<IOException> ex = new AtomicReference<>();
        ex.set(null);
        VirtualFile finalFirstRoot = firstRoot;
        ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(() -> {
            try {
                JSONObject meta = getStubMetaObject(moduleName);
                VirtualFile metaFile = finalFirstRoot.findOrCreateChildData(this, META6_JSON_NAME);
                metaFile.setBinaryContent(MetaDataJSONSerializer.serializer(meta).getBytes(CharsetToolkit.UTF8_CHARSET));
                myMeta = meta;
                myMetaFile = metaFile;

                if (shouldOpenEditor)
                    FileEditorManager.getInstance(myModule.getProject()).openFile(metaFile, true);
            }
            catch (IOException e) {
                ex.set(e);
            }
        }));
        IOException ioException = ex.get();
        if (ioException != null) {
            throw ioException;
        }
    }

    private VirtualFile calculateMetaParent() {
        if (myModule == null)
            return null;
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(myModule).getSourceRoots();
        for (VirtualFile root : sourceRoots) {
            if (!root.getName().equals("lib")) continue;
            return root.getParent();
        }
        return null;
    }

    private static JSONObject getStubMetaObject(String moduleName) {
        JSONArray authorsArray = new JSONArray();
        authorsArray.put("Write me!");
        return new JSONObject()
            .put("perl", "6.*")
            .put("name", moduleName)
            .put("version", "0.1")
            .put("description", "Write me!")
            .put("auth", "Write me!")
            .put("authors", authorsArray)
            .put("license", "Write me!")
            .put("depends", new JSONArray())
            .put("test-depends", new JSONArray())
            .put("build-depends", new JSONArray())
            .put("provides", new JSONObject())
            .put("resources", new JSONArray())
            .put("source-url", "Write me!");
    }

    public void addNamespaceToProvides(String name) {
        if (myMeta == null) return;
        String libBasedModulePath = String.format(
            "lib/%s.%s", name.replaceAll("::", "/"),
            Perl6ModuleFileType.INSTANCE.getDefaultExtension());
        if (!myMeta.has("provides"))
            myMeta.put("provides", new JSONArray());
        JSONObject provides = myMeta.getJSONObject("provides");
        provides.put(name, libBasedModulePath);
        myMeta.put("provides", provides);
        saveFile();
    }

    public void removeNamespaceFromProvides(String name) {
        if (myMeta == null) return;
        if (!myMeta.has("provides"))
            myMeta.put("provides", new JSONArray());
        JSONObject provides = myMeta.getJSONObject("provides");
        provides.remove(name);
        myMeta.put("provides", provides);
        saveFile();
    }

    @Nullable
    public String getName() {
        return myMeta != null && myMeta.has("name") ? myMeta.getString("name") : null;
    }

    public void setName(String name) {
        if (myMeta == null) return;
        myMeta.put("name", name);
        saveFile();
    }

    @Nullable
    public String getDescription() {
        return myMeta != null && myMeta.has("description") ? myMeta.getString("description") : null;
    }

    public void setDescription(String description) {
        if (myMeta == null) return;
        myMeta.put("description", description);
        saveFile();
    }

    @Nullable
    public String getVersion() {
        return myMeta != null && myMeta.has("version") ? myMeta.getString("version") : null;
    }

    public void setVersion(String version) {
        if (myMeta == null) return;
        myMeta.put("version", version);
        saveFile();
    }

    @Nullable
    public String getAuth() {
        return myMeta != null && myMeta.has("auth") ? myMeta.getString("auth") : null;
    }

    public void setAuth(String auth) {
        if (myMeta == null) return;
        myMeta.put("auth", auth);
        saveFile();
    }

    @Nullable
    public String getLicense() {
        return myMeta != null && myMeta.has("license") ? myMeta.getString("license") : null;
    }

    public void setLicense(String license) {
        if (myMeta == null) return;
        myMeta.put("license", license);
        saveFile();
    }

    @Nullable
    public String getSourceURL() {
        return myMeta != null && myMeta.has("source-url") ? myMeta.getString("source-url") : null;
    }

    public void setSourceURL(String sourceURL) {
        if (myMeta == null) return;
        myMeta.put("source-url", sourceURL);
        saveFile();
    }

    @Nullable
    public List<Object> getAuthors() {
        return myMeta != null && myMeta.has("authors") ? myMeta.getJSONArray("authors").toList() : null;
    }

    public void setAuthors(List<String> authors) {
        if (myMeta == null) return;
        myMeta.put("authors", authors);
        saveFile();
    }

    @Nullable
    public VirtualFile getMetaFile() {
        return myMetaFile;
    }

    private void saveFile() {
        if (myMetaFile == null || myMeta == null) return;
        String json = MetaDataJSONSerializer.serializer(myMeta);
        ApplicationManager.getApplication().invokeLater(() -> WriteAction.run(
            () -> {
                try {
                    if (!myMetaFile.isValid())
                        myMetaFile = myMetaFile.getParent().createChildData(this, myMetaFile.getName());
                    myMetaFile.setBinaryContent(json.getBytes(CharsetToolkit.UTF8_CHARSET));
                    triggerMetaBuild(myMetaFile);
                }
                catch (IOException e) {
                    notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
                }
            }
        ));
    }

    private void notifyMetaIssue(String message, NotificationType type, AnAction... actions) {
        if (myModule == null)
            return;
        Notification notification = new Notification(
            "raku.meta.errors", Perl6Icons.CAMELIA,
            "Raku meta error", "",
            message, type, null);
        if (myMetaFile != null) {
            notification.addAction(new AnAction(String.format("Open %s", META6_JSON_NAME)) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    if (myModule.isDisposed()) return;
                    FileEditorManager.getInstance(myModule.getProject()).openFile(myMetaFile, true);
                    notification.expire();
                }
            });
        }
        for (AnAction action : actions) {
            if (action == null) continue;
            notification.addAction(new AnAction(action.getTemplatePresentation().getText()) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    notification.expire();
                    action.actionPerformed(e);
                }
            });
        }
        Notifications.Bus.notify(notification, myModule.getProject());
    }

    private void notifyMissingMETA() {
        Notification notification = new Notification(
            "raku.meta.errors", Perl6Icons.CAMELIA,
            "Raku meta file is missing", "",
            String.format("'%s' nor '%s' files seem to be present in this module.", META_OBSOLETE_NAME, META6_JSON_NAME),
            NotificationType.WARNING, null);
        notification.addAction(new AnAction(String.format("Stub and open %s file", META6_JSON_NAME)) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                try {
                    notification.expire();
                    if (myModule == null || myModule.isDisposed()) return;
                    createStubMetaFile(myModule.getName(), null, true);
                }
                catch (IOException e1) {
                    Notifications.Bus.notify(new Notification(
                        "raku.meta.errors", Perl6Icons.CAMELIA,
                        String.format("%s error", META6_JSON_NAME),
                        String.format("Error has occurred during %s file creation", META6_JSON_NAME),
                        e1.getMessage(), NotificationType.ERROR, null));
                }
            }
        });
        Notifications.Bus.notify(notification);
    }

    private static class Perl6MetaException extends Exception {
        public final AnAction myFix;

        Perl6MetaException(String message) {
            this(message, null);
        }

        Perl6MetaException(String message, AnAction fix) {
            super(message);
            myFix = fix;
        }
    }
}
