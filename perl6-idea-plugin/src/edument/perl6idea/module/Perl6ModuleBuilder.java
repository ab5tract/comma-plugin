package edument.perl6idea.module;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.filetypes.Perl6TestFileType;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6ProjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Perl6ModuleBuilder extends ModuleBuilder implements SourcePathsBuilder {
    private static Logger LOG = Logger.getInstance(Perl6ModuleBuilder.class);
    private Perl6ProjectType type = Perl6ProjectType.PERL6_SCRIPT;
    private List<Pair<String, String>> mySourcePaths = new ArrayList<>();
    private String scriptName;
    private String moduleName;
    private String entryPointName;
    private boolean websocketSupport;
    private boolean templatingSupport;

    @Override
    public String getName() {
        return "Perl 6 Builder";
    }

    @Override
    public String getDescription() {
        return "Perl 6 builder";
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel model) {
        ContentEntry contentEntry = doAddContentEntry(model);
        if (contentEntry == null) return;
        List<Pair<String, String>> sourcePaths = getSourcePaths();
        Perl6MetaDataComponent metaData = model.getModule().getComponent(Perl6MetaDataComponent.class);
        if (sourcePaths.size() < 1) return;
        for (final Pair<String, String> sourcePathPair : sourcePaths) {
            Path sourcePath = Paths.get(sourcePathPair.first);
            File directory = sourcePath.toFile();
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + directory);
            }
            VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourcePath.toFile());
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, sourcePathPair.second.equals("t"), sourcePathPair.second);
            }
            switch (type) {
                case PERL6_SCRIPT:
                    stubScript(sourcePath, scriptName, true);
                    break;
                case PERL6_MODULE:
                    if (sourcePathPair.second.equals("lib")) {
                        stubModule(metaData, sourcePath, moduleName, true, false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty",
                                   false);
                    }
                    if (sourcePathPair.second.equals("t"))
                        stubTest(sourcePath, "00-sanity.t",
                                 Collections.singletonList(moduleName));
                    break;
                case PERL6_APPLICATION:
                    if (sourcePathPair.second.equals("lib")) {
                        stubModule(metaData, sourcePath, moduleName, true, false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty",
                                   false);
                    }
                    if (sourcePathPair.second.equals("bin"))
                        stubEntryPoint(sourcePath);
                    if (sourcePathPair.second.equals("t"))
                        stubTest(sourcePath,
                                "00-sanity.t",
                                Collections.singletonList(moduleName));
                    break;
                case CRO_WEB_APPLICATION:
                    if (sourcePathPair.second.equals("lib")) {
                        //    stubRoutes(metaData, sourcePath, moduleName, true, false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty", false);
                    } else if (sourcePathPair.second.equals("t")) {
                    } else {
                        stubCroDockerfile(sourcePath);
                        stubCroServiceFile(sourcePath);
                    }
                    break;
            }
        }
    }

    private void stubCroDockerfile(Path sourcePath) {
        Path dockerFilePath = sourcePath.resolve("Dockerfile");
        InputStream dockerFileTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/CroDockerfile");
        BufferedReader reader = new BufferedReader(new InputStreamReader(dockerFileTemplateStream, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>();

        try {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            dockerFileTemplateStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        writeCodeToPath(dockerFilePath, lines);
    }

    private void stubCroServiceFile(Path sourcePath) {
        String HOST_VARIABLE = convertToEnvName(moduleName) + "_HOST";
        String PORT_VARIABLE = convertToEnvName(moduleName) + "_PORT";
        Path croServiceFilePath = sourcePath.resolve( "service.p6");
        InputStream serviceTemplateStream = getClass().getClassLoader().getResourceAsStream("templates/service.p6.template");
        BufferedReader reader = new BufferedReader(new InputStreamReader(serviceTemplateStream, StandardCharsets.UTF_8));
        List<String> lines = new ArrayList<>();

        try {
            while (reader.ready()) {
                String line = reader.readLine();
                line = line.replaceAll("\\$\\$HOST_VARIABLE\\$\\$", HOST_VARIABLE);
                line = line.replaceAll("\\$\\$PORT_VARIABLE\\$\\$", PORT_VARIABLE);
                lines.add(line);
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        writeCodeToPath(croServiceFilePath, lines);
    }

    private static String convertToEnvName(String name) {
        return name.replaceAll("[^\\w_]", "_");
    }

    private void stubEntryPoint(Path moduleLibraryPath) {
        Path entryPath = moduleLibraryPath.resolve(entryPointName);
        List<String> lines = Arrays.asList(
                "#!/usr/bin/env perl6",
                String.format("use %s;", moduleName)
        );
        writeCodeToPath(entryPath, lines);
    }

    public static String stubModule(Perl6MetaDataComponent metaData, Path moduleLibraryPath,
                                    String moduleName, boolean firstModule, boolean shouldOpenEditor,
                                    VirtualFile root, String moduleType, boolean isUnitScoped) {
        if (firstModule) {
            try {
                metaData.createStubMetaFile(root, shouldOpenEditor);
            }
            catch (IOException e) {
                LOG.warn(e);
            }
        }
        if (moduleLibraryPath.endsWith("lib")) {
            ApplicationManager.getApplication().invokeLater(() -> {
                metaData.addNamespaceToProvides(moduleName);
            });
        }
        String modulePath = Paths.get(moduleLibraryPath.toString(), moduleName.split("::")) + "." + Perl6ModuleFileType.INSTANCE.getDefaultExtension();
        new File(modulePath).getParentFile().mkdirs();
        writeCodeToPath(Paths.get(modulePath), getModuleCodeByType(moduleType, moduleName, isUnitScoped));
        if (moduleType.equals("Monitor")) {
            metaData.addDepends("OO::Monitors");
        }
        return modulePath;
    }

    private static List<String> getModuleCodeByType(String type, String name, boolean isUnitScoped) {
        if (isUnitScoped) {
            switch (type) {
                case "Class":
                case "Role":
                case "Grammar":
                case "Module":
                    return Arrays.asList(String.format("unit %s %s;", type.toLowerCase(Locale.ENGLISH), name), "");
                case "Monitor":
                    return Arrays.asList(
                        "use OO::Monitors;", "",
                        String.format("unit %s %s;", type.toLowerCase(Locale.ENGLISH), name), "");
                default:
                    return Collections.singletonList("");
            }
        }
        else {
            switch (type) {
                case "Class":
                case "Role":
                case "Grammar":
                case "Module":
                    return Arrays.asList(String.format("%s %s {", type.toLowerCase(Locale.ENGLISH), name), "", "}");
                case "Monitor":
                    return Arrays.asList(
                        "use OO::Monitors;", "",
                        String.format("%s %s {", type.toLowerCase(Locale.ENGLISH), name), "", "}");
                default:
                    return Collections.singletonList("");
            }
        }
    }

    public static String stubTest(Path testDirectoryPath, String fileName, List<String> imports) {
        Path testPath = testDirectoryPath.resolve(fileName);
        // If no extension, add default `.t`
        if (!testPath.toString().contains("."))
            testPath = Paths.get(testDirectoryPath.toString(), fileName + "." + Perl6TestFileType.INSTANCE.getDefaultExtension());
        List<String> lines = new LinkedList<>();
        imports.forEach(i -> lines.add(String.format("use %s;", i)));
        lines.addAll(Arrays.asList("use Test;", "", "done-testing;"));
        writeCodeToPath(testPath, lines);
        return testPath.toString();
    }

    public static String stubScript(Path moduleLibraryPath, String scriptName, boolean shouldFill) {
        if (!scriptName.endsWith(".pl6") && !scriptName.endsWith(Perl6ScriptFileType.INSTANCE.getDefaultExtension()))
            scriptName += "." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        List<String> lines = Arrays.asList(
                "#!/usr/bin/env perl6",
                "", "",
                "sub MAIN() { }"
        );
        Path path = moduleLibraryPath.resolve(scriptName);
        writeCodeToPath(path, shouldFill ? lines : new ArrayList<>());
        return path.toString();
    }

    private static void writeCodeToPath(Path codePath, List<String> lines) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (!codePath.getParent().toFile().exists())
                    Files.createDirectories(codePath.getParent());
                Files.write(codePath, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                Logger.getInstance(Perl6ModuleBuilder.class).error(e);
            }
        });
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return Perl6ModuleType.getInstance();
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk == Perl6SdkType.getInstance();
    }

    @Override
    public String getGroupName() {
        return "Perl 6";
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        if (mySourcePaths.size() != 0) return mySourcePaths;
        switch (type) {
            case PERL6_SCRIPT:
                addPath(mySourcePaths, "");
                break;
            case PERL6_MODULE:
                addPath(mySourcePaths, "lib");
                addPath(mySourcePaths, "t");
                break;
            case PERL6_APPLICATION:
                addPath(mySourcePaths, "lib");
                addPath(mySourcePaths, "t");
                addPath(mySourcePaths, "bin");
                break;
            case CRO_WEB_APPLICATION:
                addPath(mySourcePaths, "");
                addPath(mySourcePaths, "lib");
                addPath(mySourcePaths, "t");
                break;
        }
        return mySourcePaths;
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        mySourcePaths = sourcePaths;
    }

    private void addPath(List<Pair<String, String>> paths, String dirName) {
        String contentEntryPath = getContentEntryPath();
        if (contentEntryPath == null) return;
        String path = Paths.get(contentEntryPath, dirName).toString();
        paths.add(Pair.create(path, ""));
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        mySourcePaths.add(sourcePathInfo);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{ new Perl6ModuleWizardStep(this) };
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        TypeWizardStepForm step = new TypeWizardStepForm(this);
        Disposer.register(parentDisposable, step);
        return step;
    }

    Perl6ProjectType getType() {
        return type;
    }

    void setType(Perl6ProjectType type) {
        this.type = type;
    }

    void setEntryPointName(String entryPointName) {
        this.entryPointName = entryPointName;
    }

    void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    void setTemplatingSupport(boolean templatingSupport) {
        this.templatingSupport = templatingSupport;
    }

    void setWebsocketSupport(boolean websocketSupport) {
        this.websocketSupport = websocketSupport;
    }

    //@Nullable
    //@Override
    //public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
    //    final ModuleNameLocationSettings nameField = settingsStep.getModuleNameLocationSettings();
    //    if (moduleName != null && nameField != null)
    //        nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(moduleName));
    //    return super.modifySettingsStep(settingsStep);
    //}
}
