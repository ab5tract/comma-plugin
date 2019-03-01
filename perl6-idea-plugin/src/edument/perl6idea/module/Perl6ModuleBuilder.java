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
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.filetypes.Perl6TestFileType;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.utils.Perl6ProjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
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

    @Override
    public String getName() {
        return "Perl 6 Builder";
    }

    @Override
    public String getDescription() {
        return "Perl 6 builder";
    }

    @Override
    public void setupRootModel(ModifiableRootModel model) {
        ContentEntry contentEntry = doAddContentEntry(model);
        if (contentEntry == null) return;
        List<Pair<String, String>> sourcePaths = getSourcePaths();
        Perl6MetaDataComponent metaData = model.getModule().getComponent(Perl6MetaDataComponent.class);
        if (sourcePaths.size() < 1) return;
        for (final Pair<String, String> sourcePathPair : sourcePaths) {
            String sourcePath = sourcePathPair.first;
            File directory = new File(sourcePath);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IllegalStateException("Could not create directory: " + directory);
            }
            VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemDependentName(sourcePath));
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, Objects.equals(sourcePath, "t"), sourcePathPair.second);
            }
            switch (type) {
                case PERL6_SCRIPT:
                    stubScript(sourcePath, scriptName);
                    break;
                case PERL6_MODULE:
                    if (sourcePath.endsWith("lib")) {
                        stubModule(metaData, sourcePath, moduleName, true, false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty",
                                   false);
                    }
                    if (sourcePath.endsWith("t"))
                        stubTest(sourcePath, "00-sanity.t",
                                 Collections.singletonList(moduleName));
                    break;
                case PERL6_APPLICATION:
                    if (sourcePath.endsWith("lib")) {
                        stubModule(metaData, sourcePath, moduleName, true, false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty",
                                   false);
                    }
                    if (sourcePath.endsWith("bin"))
                        stubEntryPoint(sourcePath);
                    if (sourcePath.endsWith("t"))
                        stubTest(sourcePath,
                                "00-sanity.t",
                                Collections.singletonList(moduleName));
                    break;
            }
        }
    }

    private void stubEntryPoint(String moduleLibraryPath) {
        Path entryPath = Paths.get(moduleLibraryPath, entryPointName);
        List<String> lines = Arrays.asList(
                "#!/usr/bin/env perl6",
                String.format("use %s;", moduleName)
        );
        writeCodeToPath(entryPath, lines);
    }

    public static String stubModule(Perl6MetaDataComponent metaData, String moduleLibraryPath,
                                    String moduleName, boolean firstModule, boolean shouldOpenEditor,
                                    VirtualFile root, String type, boolean isUnitScoped) {
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
        String modulePath = Paths.get(moduleLibraryPath, moduleName.split("::")) + "." + Perl6ModuleFileType.INSTANCE.getDefaultExtension();
        new File(modulePath).getParentFile().mkdirs();
        writeCodeToPath(Paths.get(modulePath), getModuleCodeByType(type, moduleName, isUnitScoped));
        return modulePath;
    }

    private static List<String> getModuleCodeByType(String type, String name, boolean isUnitScoped) {
        if (isUnitScoped) {
            switch (type) {
                case "Class": {
                    return Arrays.asList(String.format("unit class %s;", name), "");
                }
                case "Role": {
                    return Arrays.asList(String.format("unit role %s;", name), "");
                }
                case "Grammar": {
                    return Arrays.asList(String.format("unit grammar %s;", name), "");
                }
                case "Module": {
                    return Arrays.asList(String.format("unit module %s;", name), "");
                }
            }
        } else {
            switch (type) {
                case "Class": {
                    return Arrays.asList(String.format("class %s {", name), "", "}");
                }
                case "Role": {
                    return Arrays.asList(String.format("role %s {", name), "", "}");
                }
                case "Grammar": {
                    return Arrays.asList(String.format("grammar %s {", name), "", "}");
                }
                case "Module": {
                    return Arrays.asList(String.format("module %s {", name), "", "}");
                }
            }
        }
        return Collections.singletonList("");
    }

    public static String stubTest(String testDirectoryPath, String fileName, List<String> imports) {
        Path testPath = Paths.get(testDirectoryPath, fileName);
        // If no extension, add default `.t`
        if (!testPath.toString().contains("."))
            testPath = Paths.get(testDirectoryPath, fileName + "." + Perl6TestFileType.INSTANCE.getDefaultExtension());
        List<String> lines = new LinkedList<>();
        imports.forEach(i -> lines.add(String.format("use %s;", i)));
        lines.addAll(Arrays.asList("use Test;", "", "done-testing;"));
        writeCodeToPath(testPath, lines);
        return testPath.toString();
    }

    public static String stubScript(String moduleLibraryPath, String scriptName) {
        Path scriptPath = Paths.get(moduleLibraryPath, scriptName);
        String stringPath = scriptPath.toString();
        if (!stringPath.endsWith(".pl6") && !stringPath.endsWith(Perl6ScriptFileType.INSTANCE.getDefaultExtension()))
            stringPath += "." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();

        List<String> lines = Arrays.asList(
                "#!/usr/bin/env perl6",
                "", "",
                "sub MAIN() { }"
        );
        writeCodeToPath(scriptPath, lines);
        return stringPath;
    }

    private static void writeCodeToPath(Path codePath, List<String> lines) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (Files.notExists(codePath.getParent()))
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
                mySourcePaths.add(Pair.create(getContentEntryPath(), ""));
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

    //@Nullable
    //@Override
    //public ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep) {
    //    final ModuleNameLocationSettings nameField = settingsStep.getModuleNameLocationSettings();
    //    if (moduleName != null && nameField != null)
    //        nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(moduleName));
    //    return super.modifySettingsStep(settingsStep);
    //}
}
