package edument.perl6idea.module;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.util.Function;
import edument.perl6idea.Perl6Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Perl6MetaDataComponent implements ModuleComponent {
    private final Module myModule;
    private VirtualFile myMetaFile = null;
    private JSONObject myMeta = null;

    public Perl6MetaDataComponent(Module module) {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                if (!event.isFromSave() ||
                    !(event.getFileName().equals("META6.json") ||
                      event.getFileName().equals("META.list"))) return;
                try {
                    myMeta = checkMetaSanity();
                    saveFile();
                }
                catch (Perl6MetaException e) {
                    if (e.myFix != null)
                        notifyMetaIssue(e.getMessage(), NotificationType.ERROR, e.myFix);
                    else
                        notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
                }
            }
        });
        myModule = module;
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        for (VirtualFile root : contentRoots) {
            boolean isLibRoot = root.getName().equals("lib");
            boolean hasLibRoot = root.findChild("lib") != null;
            if (isLibRoot || hasLibRoot) {
                VirtualFile metaFile = getMetaFromContentRoot(root, hasLibRoot, "META6.json");
                // Try to search by obsolete 'META.info' name and warn about it if present
                if (metaFile == null) {
                    metaFile = getMetaFromContentRoot(root, hasLibRoot, "META.info");
                    if (metaFile != null) {
                        VirtualFile finalMetaFile = metaFile;
                        notifyMetaIssue(
                            "Obsolete 'META.info' file name is used instead of 'META6.json'",
                            NotificationType.ERROR,
                            new AnAction("Rename to META6.json") {
                                @Override
                                public void actionPerformed(AnActionEvent event) {
                                    try {
                                        finalMetaFile.rename(this, "META6.json");
                                    }
                                    catch (IOException ex) {
                                        Notifications.Bus.notify(
                                            new Notification(
                                                "Perl 6 meta error","Perl 6 META error",
                                                "Could not rename META file: " + ex.getMessage(),
                                                NotificationType.ERROR));
                                    }
                                }
                            });
                    }
                }

                // If everything fails, notify about META absence
                // and suggest to stub it
                if (metaFile == null) {
                    notifyMissingMETA();
                    return;
                }
                try {
                    myMetaFile = metaFile;
                    myMeta = checkMetaSanity();
                }
                catch (Perl6MetaException e) {
                    notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
                }
            }
        }
    }

    @Nullable
    private static VirtualFile getMetaFromContentRoot(VirtualFile root, boolean hasLibRoot, String name) {
        return hasLibRoot ? root.findChild(name) : root.getParent().findChild(name);
    }

    private JSONObject checkMetaSanity() throws Perl6MetaException {
        if (myMetaFile.exists()) {
            try {
                String content = new String(myMetaFile.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET);
                return checkMetaSanity(new JSONObject(content));
            }
            catch (IOException|JSONException e) {
                notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
            }
        }
        return null;
    }

    /* Enforces number of rules for meta to check on start and every saving */
    public static JSONObject checkMetaSanity(JSONObject meta) throws Perl6MetaException {
        checkParameter(meta, "name", v -> v instanceof String, "string");
        checkParameter(meta, "description", v -> v instanceof String, "string");
        checkParameter(meta, "version", v -> v instanceof String, "string");
        checkParameter(meta, "auth", v -> v instanceof String, "string");
        checkParameter(meta, "license", v -> v instanceof String, "string");

        checkParameter(meta, "depends", v ->
            v instanceof JSONArray && ((JSONArray)v).toList().stream().allMatch(iv -> iv instanceof String), "string array");
        checkParameter(meta, "test-depends", v ->
            v instanceof JSONArray && ((JSONArray)v).toList().stream().allMatch(iv -> iv instanceof String), "string array");
        checkParameter(meta, "build-depends", v ->
            v instanceof JSONArray && ((JSONArray)v).toList().stream().allMatch(iv -> iv instanceof String), "string array");

        checkParameter(meta, "provides", v ->
            v instanceof JSONObject && ((JSONObject)v).toMap().values().stream().allMatch(iv -> iv instanceof String), "provides object");
        return meta;
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

    public boolean isMetaDataExist() {
        return myMeta != null;
    }

    public List<String> getDepends() {
        return getDependsInternal("depends");
    }

    public void addDepends(String name) {
        if (!isMetaDataExist()) return;
        JSONArray depends = myMeta.getJSONArray("depends");
        depends.put(name);
        myMeta.put("depends", depends);
        saveFile();
    }

    public List<String> getTestDepends() {
        return getDependsInternal("test-depends");
    }

    public List<String> getBuildDepends() {
        return getDependsInternal("build-depends");
    }

    private List<String> getDependsInternal(String key) {
        if (!isMetaDataExist()) return new ArrayList<>();
        JSONArray depends = (JSONArray)myMeta.get(key);
        List<String> result = new ArrayList<>();
        depends.toList().forEach(o -> result.add(normalizeDepends((String)o)));
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

    public void createStubMetaFile(VirtualFile firstRoot, boolean shouldOpenEditor) throws IOException {
        if (firstRoot == null)
            firstRoot = calculateMetaParent();
        if (firstRoot == null) {
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
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                JSONObject meta = getStubMetaObject();
                VirtualFile metaFile = finalFirstRoot.createChildData(this, "META6.json");
                WriteAction.run(() -> metaFile.setBinaryContent(meta.toString(4).getBytes(CharsetToolkit.UTF8_CHARSET)));

                myMeta = meta;
                myMetaFile = metaFile;

                if (shouldOpenEditor)
                    FileEditorManager.getInstance(myModule.getProject()).openFile(metaFile, true);
            }
            catch (IOException e) {
                ex.set(e);
            }
        });
        IOException ioException = ex.get();
        if (ioException != null) {
            throw ioException;
        }
    }

    private VirtualFile calculateMetaParent() {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(myModule).getSourceRoots();
        for (VirtualFile root : contentRoots) {
            if (!root.getName().equals("lib")) continue;
            return root.getParent();
        }
        return null;
    }

    private JSONObject getStubMetaObject() {
        return new JSONObject()
            .put("perl", "6.*")
            .put("name", myModule.getName())
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

    public void addNamespaceToProvides(String name, String path) {
        if (!isMetaDataExist()) return;
        JSONObject provides = myMeta.getJSONObject("provides");
        provides.put(name, path);
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
        return isMetaDataExist() ? myMeta.getString("name") : null;
    }

    public void setName(String name) {
        myMeta.put("name", name); saveFile();
    }

    @Nullable
    public String getDescription() {
        return isMetaDataExist() ? myMeta.getString("description") : null;
    }

    public void setDescription(String description) {
        myMeta.put("description", description); saveFile();
    }

    @Nullable
    public String getVersion() {
        return isMetaDataExist() ? myMeta.getString("version") : null;
    }

    public void setVersion(String version) {
        myMeta.put("version", version); saveFile();
    }

    @Nullable
    public String getAuth() {
        return isMetaDataExist() ? myMeta.getString("auth") : null;
    }

    public void setAuth(String auth) {
        myMeta.put("auth", auth); saveFile();
    }

    @Nullable
    public String getLicense() {
        return isMetaDataExist() ? myMeta.getString("license") : null;
    }

    public void setLicense(String license) {
        myMeta.put("license", license); saveFile();
    }

    private void saveFile() {
        if (myMetaFile == null || myMeta == null) return;
        String json = myMeta.toString(4);
        try {
            WriteAction.run(() -> myMetaFile.setBinaryContent(json.getBytes(CharsetToolkit.UTF8_CHARSET)));
        }
        catch (IOException e) {
            notifyMetaIssue(e.getMessage(), NotificationType.ERROR);
        }
    }

    private void notifyMetaIssue(String message, NotificationType type, AnAction... actions) {
        Notification notification = new Notification(
            "Perl 6 meta error", Perl6Icons.CAMELIA,
            "Perl 6 meta error", "",
            message, type, null);
        if (myMetaFile != null) {
            notification.addAction(new AnAction("Open META6.json") {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    FileEditorManager.getInstance(myModule.getProject()).openFile(myMetaFile, true);
                }
            });
        }
        for (AnAction action : actions) {
            notification.addAction(action);
        }
        Notifications.Bus.notify(notification);
    }

    private void notifyMissingMETA() {
        Notification notification = new Notification(
            "Perl 6 meta error", Perl6Icons.CAMELIA,
            "Perl 6 meta file is missing", "",
            "'META.info' nor 'META6.json' files seem to be present in this module.",
            NotificationType.WARNING, null);
        notification.addAction(new AnAction("Stub and open META6.json file") {
            @Override
            public void actionPerformed(AnActionEvent e) {
                try {
                    createStubMetaFile(null, true);
                }
                catch (IOException e1) {
                    Notifications.Bus.notify(new Notification(
                        "Perl 6 meta error", Perl6Icons.CAMELIA,
                        "META6.json error", "Error has occurred during META6.json file creation",
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
