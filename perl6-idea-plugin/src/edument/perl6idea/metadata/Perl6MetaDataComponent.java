package edument.perl6idea.metadata;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.vfs.*;
import com.intellij.util.Function;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.library.Perl6LibraryType;
import edument.perl6idea.module.Perl6ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Perl6MetaDataComponent implements ModuleComponent {
    public static final String META6_JSON_NAME = "META6.json";
    public static final String META_OBSOLETE_NAME = "META.info";
    private Module myModule = null;
    private VirtualFile myMetaFile = null;
    private JSONObject myMeta = null;

    public Perl6MetaDataComponent(Module module) {
        String name = module.getModuleTypeName();

        if (name == null || !name.equals(Perl6ModuleType.getInstance().getId()))
            return;

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                if (!event.isFromSave() ||
                    !(event.getFileName().equals(META6_JSON_NAME) ||
                      event.getFileName().equals(META_OBSOLETE_NAME))) return;
                myMeta = checkMetaSanity();
                saveFile();
            }
        });
        myModule = module;
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
            syncExternalLibraries(myMeta);
        }
    }

    private void syncExternalLibraries(JSONObject meta) {
        Sdk sdk = ProjectRootManager.getInstance(myModule.getProject()).getProjectSdk();
        Application application = ApplicationManager.getApplication();
        if (sdk == null) {
            application.invokeLater(() -> {
                application.runWriteAction(() -> {
                    ModifiableRootModel model = ModuleRootManager.getInstance(myModule).getModifiableModel();
                    for (OrderEntry entry : model.getOrderEntries()) {
                        model.removeOrderEntry(entry);
                    }
                    model.commit();
                });
            });
            return;
        }

        final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(myModule);
        List<String> libraryNames = new ArrayList<>();
        List<String> missingEntries = new ArrayList<>();
        moduleRootManager.orderEntries().forEachLibrary(library -> {
            libraryNames.add(library.getName());
            return true;
        });
        JSONArray depends = meta.getJSONArray("depends");
        outer : for (Object dependencyName : depends) {
            if (dependencyName instanceof String) {
                for (String libraryName : libraryNames) {
                    if (Objects.equals(libraryName, dependencyName))
                        continue outer;
                }
                missingEntries.add((String)dependencyName);
            }
        }

        if (missingEntries.isEmpty())
            return;

        application.invokeLater(() -> {
            application.runWriteAction(() -> {
                for (String missingEntry : missingEntries) {
                    String url = String.format("raku://%d:%s", sdk.getName().hashCode(), missingEntry);
                    ModuleRootModificationUtil.updateModel(myModule, model -> {
                        LibraryEx library = (LibraryEx)model.getModuleLibraryTable().createLibrary(missingEntry);
                        LibraryEx.ModifiableModelEx libraryModel = library.getModifiableModel();
                        libraryModel.setKind(Perl6LibraryType.LIBRARY_KIND);

                        for (String rootUrl : Collections.singletonList(url)) {
                            libraryModel.addRoot(rootUrl, OrderRootType.SOURCES);
                        }

                        LibraryOrderEntry entry = model.findLibraryOrderEntry(library);
                        assert entry != null : library;
                        entry.setScope(DependencyScope.COMPILE);
                        entry.setExported(false);

                        application.invokeAndWait(() -> WriteAction.run(libraryModel::commit));
                    });
                }
            });
        });
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
                    public void actionPerformed(AnActionEvent event) {
                        ApplicationManager.getApplication().invokeLater(
                            () -> WriteAction.run(() -> {
                                try {
                                    metaFile.rename(this, META6_JSON_NAME);
                                }
                                catch (IOException ex) {
                                    Notifications.Bus.notify(
                                        new Notification(
                                            "Perl 6 meta error", "Perl 6 META error",
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
            catch (IOException|JSONException|Perl6MetaException e) {
                if (e instanceof Perl6MetaException)
                    notifyMetaIssue(e.getMessage(), NotificationType.ERROR, ((Perl6MetaException)e).myFix);
                else
                    notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
            }
        }
        return meta;
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

    public void triggerMetaBuild(@NotNull VirtualFile metaFile) {
        myMetaFile = metaFile;
        myMeta = checkMetaSanity();
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
        if (!isMetaDataExist() || !myMeta.has(key)) return;
        Object depends = myMeta.has(key) ? myMeta.get(key) : new JSONArray();
        if (!(depends instanceof JSONArray)) return;
        JSONArray dependsArray = (JSONArray)depends;
        if (dependsArray.toList().contains(name))
            return;
        dependsArray.put(name);
        myMeta.put(key, dependsArray);
        saveFile();
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
        if (!isMetaDataExist()) return new ArrayList<>();
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
        if (!isMetaDataExist()) return;
        myMeta.put(key, new JSONArray(buildDepends));
        saveFile();
    }

    public Collection<String> getProvidedNames() {
        if (!isMetaDataExist()) return new ArrayList<>();
        List<String> names = new ArrayList<>();
        Object provides = myMeta.get("provides");
        for (Object value : ((JSONObject)provides).toMap().keySet()) {
            names.add((String)value);
        }
        return names;
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
        if (firstRoot == null)
            firstRoot = calculateMetaParent();
        if (firstRoot == null) {
            if (myModule == null)
                return;
            VirtualFile file = FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                myModule.getProject(), myModule.getProject().getBaseDir());
            if (file == null) {
                notifyMetaIssue("Directory was not selected, meta file creation is canceled", NotificationType.INFORMATION);
                return;
            } else {
                firstRoot = file;
            }
        }

        AtomicReference<IOException> ex = new AtomicReference<>();
        ex.set(null);
        VirtualFile finalFirstRoot = firstRoot;
        ApplicationManager.getApplication().invokeLater(() -> WriteAction.run(() -> {
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
        return new JSONObject()
            .put("perl", "6.*")
            .put("name", moduleName)
            .put("version", "0.1")
            .put("description", "Write me!")
            .put("auth", "Write me!")
            .put("license", "Write me!")
            .put("depends", new JSONArray())
            .put("test-depends", new JSONArray())
            .put("build-depends", new JSONArray())
            .put("provides", new JSONObject())
            .put("resources", new JSONArray())
            .put("source-url", "Write me!");
    }

    public void addNamespaceToProvides(String name) {
        if (!isMetaDataExist()) return;
        String libBasedModulePath = String.format(
            "lib/%s.%s", name.replaceAll("::", "/"),
            Perl6ModuleFileType.INSTANCE.getDefaultExtension());
        JSONObject provides = myMeta.getJSONObject("provides");
        provides.put(name, libBasedModulePath);
        myMeta.put("provides", provides);
        saveFile();
    }

    public void removeNamespaceToProvides(String name) {
        if (!isMetaDataExist()) return;
        JSONObject provides = myMeta.getJSONObject("provides");
        provides.remove(name);
        myMeta.put("provides", provides);
        saveFile();
    }

    @Nullable
    public String getName() {
        return isMetaDataExist() && myMeta.has("name") ? myMeta.getString("name") : null;
    }

    public void setName(String name) {
        myMeta.put("name", name); saveFile();
    }

    @Nullable
    public String getDescription() {
        return isMetaDataExist() && myMeta.has("description") ? myMeta.getString("description") : null;
    }

    public void setDescription(String description) {
        myMeta.put("description", description); saveFile();
    }

    @Nullable
    public String getVersion() {
        return isMetaDataExist() && myMeta.has("version") ? myMeta.getString("version") : null;
    }

    public void setVersion(String version) {
        myMeta.put("version", version); saveFile();
    }

    @Nullable
    public String getAuth() {
        return isMetaDataExist() && myMeta.has("auth") ? myMeta.getString("auth") : null;
    }

    public void setAuth(String auth) {
        myMeta.put("auth", auth); saveFile();
    }

    @Nullable
    public String getLicense() {
        return isMetaDataExist() && myMeta.has("license") ? myMeta.getString("license") : null;
    }

    public void setLicense(String license) {
        myMeta.put("license", license); saveFile();
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
                }
                catch (IOException e) {
                    notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
                }
            }
        ));
    }

    private void notifyMetaIssue(String message, NotificationType type, AnAction... actions) {
        Notification notification = new Notification(
            "Perl 6 meta error", Perl6Icons.CAMELIA,
            "Perl 6 meta error", "",
            message, type, null);
        if (myMetaFile != null) {
            notification.addAction(new AnAction(String.format("Open %s", META6_JSON_NAME)) {
                @Override
                public void actionPerformed(AnActionEvent e) {
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
                public void actionPerformed(AnActionEvent e) {
                    notification.expire();
                    action.actionPerformed(e);
                }
            });
        }
        Notifications.Bus.notify(notification, myModule.getProject());
    }

    private void notifyMissingMETA() {
        Notification notification = new Notification(
            "Perl 6 meta error", Perl6Icons.CAMELIA,
            "Perl 6 meta file is missing", "",
            String.format("'%s' nor '%s' files seem to be present in this module.", META_OBSOLETE_NAME, META6_JSON_NAME),
            NotificationType.WARNING, null);
        notification.addAction(new AnAction(String.format("Stub and open %s file", META6_JSON_NAME)) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                try {
                    notification.expire();
                    if (myModule.isDisposed()) return;
                    createStubMetaFile(myModule.getName(), null, true);
                }
                catch (IOException e1) {
                    Notifications.Bus.notify(new Notification(
                        "Perl 6 meta error", Perl6Icons.CAMELIA,
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

        public Perl6MetaException(String message) {
            this(message, null);
        }

        public Perl6MetaException(String message, AnAction fix) {
            super(message);
            myFix = fix;
        }
    }
}
