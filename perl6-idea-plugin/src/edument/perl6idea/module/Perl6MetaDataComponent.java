package edument.perl6idea.module;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.glass.ui.Application;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Perl6MetaDataComponent implements ModuleComponent {
    private final Module myModule;
    private VirtualFile myMetaFile = null;
    private JSONObject myMeta = null;
    private JSONObject myProvides = null;
    private String name = null;
    private String description = null;
    private String version = null;
    private String auth = null;
    private String license = null;
    private List<String> myDepends = new ArrayList<>();
    private List<String> myTestDepends = new ArrayList<>();
    private List<String> myBuildDepends = new ArrayList<>();

    public Perl6MetaDataComponent(Module module) {
        myModule = module;
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        for (VirtualFile root : contentRoots) {
            boolean hasLib = root.findChild("lib") != null;
            if (hasLib) {
                VirtualFile metaFile = root.findChild("META6.json");
                if (metaFile == null) {
                    notifyMissingMETA();
                    return;
                }
                try {
                    String content = new String(metaFile.contentsToByteArray(), CharsetToolkit.UTF8_CHARSET);
                    myMeta = new JSONObject(content);
                    myMetaFile = metaFile;
                    populateCache();
                }
                catch (IOException e) {
                    notifyMetaIssue(e);
                    return;
                }
                catch (JSONException e) {
                    notifyBrokenMeta(e);
                    return;
                }
            }
        }
    }

    private void populateCache() {
        // Provides
        if (myMeta.has("provides")) {
            Object provides = myMeta.get("provides");
            if (provides instanceof JSONObject)
                this.myProvides = (JSONObject)provides;
        }

        // Depends
        if (myMeta.has("depends")) {
            Object depends = myMeta.get("depends");
            if (depends instanceof JSONArray)
                ((JSONArray)depends).toList().stream().filter(d -> d instanceof String).map(d -> (String)d).forEach(myDepends::add);
        }
        // Test Depends
        if (myMeta.has("test-depends")) {
            Object testDepends = myMeta.get("test-depends");
            if (testDepends instanceof JSONArray)
                ((JSONArray)testDepends).toList().stream().filter(d -> d instanceof String).map(d -> (String)d).forEach(myTestDepends::add);
        }
        // Build Depends
        if (myMeta.has("build-depends")) {
            Object buildDepends = myMeta.get("build-depends");
            if (buildDepends instanceof JSONArray)
                ((JSONArray)buildDepends).toList().stream().filter(d -> d instanceof String).map(d -> (String)d).forEach(myBuildDepends::add);
        }

        // Name
        if (myMeta.has("name")) {
            Object name = myMeta.get("name");
            if (name instanceof String)
                this.name = (String)name;
        }
        // Description
        if (myMeta.has("description")) {
            Object description = myMeta.get("description");
            if (description instanceof String)
                this.description = (String)description;
        }
        // Version
        if (myMeta.has("version")) {
            Object version = myMeta.get("version");
            if (version instanceof String)
                this.version = (String)version;
        }
        // Auth
        if (myMeta.has("auth")) {
            Object auth = myMeta.get("auth");
            if (auth instanceof String)
                this.auth = (String)auth;
        }
        // License
        if (myMeta.has("license")) {
            Object license = myMeta.get("license");
            if (license instanceof String)
                this.license = (String)license;
        }
    }

    public boolean isMetadataFile(String path) {
        return path.equals("META6.json") || path.equals("META.info");
    }

    public boolean isMetaDataExist() {
        return myMeta != null;
    }

    public List<String> getDepends() {
        return myDepends.stream().map(this::normalizeDepends).collect(Collectors.toList());
    }

    public void setDepends(List<String> depends) {
        myDepends = depends;
        saveFile();
    }

    public List<String> getTestDepends() {
        return myTestDepends.stream().map(this::normalizeDepends).collect(Collectors.toList());
    }

    public void setTestDepends(List<String> testDepends) {
        myTestDepends = testDepends;
        saveFile();
    }

    public List<String> getBuildDepends() {
        return myBuildDepends.stream().map(this::normalizeDepends).collect(Collectors.toList());
    }

    public void setBuildDepends(List<String> buildDepends) {
        myBuildDepends = buildDepends;
        saveFile();
    }

    private String normalizeDepends(String name) {
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
        if (firstRoot == null)
            throw new IOException("No parent present.");

        AtomicReference<IOException> ex = new AtomicReference<>();
        ex.set(null);
        VirtualFile finalFirstRoot = firstRoot;
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                JSONObject meta = getStubMetaObject();
                VirtualFile parent = finalFirstRoot.getParent();
                VirtualFile metaFile = parent.createChildData(this, "META6.json");
                WriteAction.run(() -> metaFile.setBinaryContent(meta.toString(4).getBytes(CharsetToolkit.UTF8_CHARSET)));

                myMeta = meta;
                myMetaFile = metaFile;
                populateCache();

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
        .put("authors", new JSONArray())
        .put("license", "Choose me!")
        .put("depends", new JSONArray())
        .put("provides", new JSONObject())
        .put("resources", new JSONArray())
        .put("source-url", "Write me!");
    }

    public void addNamespaceToProvides(String name, String path) {
        if (myProvides != null) {
            myProvides.put(name, path);
            saveFile();
        }
    }

    public void removeNamespaceToProvides(String name) {
        if (myProvides != null) {
            myProvides.remove(name);
            saveFile();
        }
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        saveFile();
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        saveFile();
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        saveFile();
    }

    @Nullable
    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
        saveFile();
    }

    @Nullable
    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
        saveFile();
    }

    private void saveFile() {
        if (myMetaFile == null || myMeta == null) return;
        myMeta.put("name", name);
        myMeta.put("description", description);
        myMeta.put("version", version);
        myMeta.put("auth", auth);
        myMeta.put("license", license);
        myMeta.put("depends", new JSONArray(myDepends));
        myMeta.put("test-depends", new JSONArray(myTestDepends));
        myMeta.put("build-depends", new JSONArray(myBuildDepends));
        myMeta.put("provides", myProvides);
        String json = myMeta.toString(4);
        try {
            WriteAction.run(() -> myMetaFile.setBinaryContent(json.getBytes(CharsetToolkit.UTF8_CHARSET)));
        }
        catch (IOException e) {
            // Just ignore exceptions on save,
            // what we could notify about is
            // done in constructor
            return;
        }
    }

    private void notifyBrokenMeta(JSONException e) {

    }

    private void notifyMetaIssue(IOException e) {

    }

    private void notifyMissingMETA() {

    }

    public void addDepends(String name) {
        myDepends.add(name);
        saveFile();
    }
}
