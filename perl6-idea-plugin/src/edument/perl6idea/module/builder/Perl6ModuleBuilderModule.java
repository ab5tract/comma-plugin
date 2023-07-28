package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.filetypes.Perl6TestFileType;
import edument.perl6idea.language.RakuLanguageVersion;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Perl6ModuleBuilderModule implements Perl6ModuleBuilderGeneric {
    private static final Logger LOG = Logger.getInstance(Perl6ModuleBuilderModule.class);
    private String myModuleName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model,
                                     Path path,
                                     RakuLanguageVersion languageVersion) {
        Perl6MetaDataComponent metaData = model.getModule().getService(Perl6MetaDataComponent.class);
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            stubModule(metaData, path, myModuleName, true,
                       false, sourceRoot == null ? null : sourceRoot.getParent(), "Empty",
                       false, languageVersion);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            stubTest(path, "00-sanity.t",
                     Collections.singletonList(myModuleName), languageVersion);
        }
    }

    @Override
    public void loadFromDialogData(Map<String, String> data) {
        myModuleName = data.get(Perl6ModuleWizardStep.MODULE_NAME);
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"lib", "t"};
    }

    public static String stubModule(Perl6MetaDataComponent metaData,
                                    Path moduleLibraryPath,
                                    String moduleName,
                                    boolean firstModule,
                                    boolean shouldOpenEditor,
                                    VirtualFile root,
                                    String moduleType,
                                    boolean isUnitScoped,
                                    RakuLanguageVersion languageVersion) {
        if (firstModule) {
            try {
                metaData.createStubMetaFile(moduleName, root, shouldOpenEditor);
            }
            catch (IOException e) {
                LOG.warn(e);
            }
        }
        if (moduleLibraryPath.endsWith("lib")) {
            ApplicationManager.getApplication().invokeLater(() -> metaData.addNamespaceToProvides(moduleName, Perl6ModuleFileType.INSTANCE.getDefaultExtension()));
        }
        String modulePath = Paths.get(moduleLibraryPath.toString(), moduleName.split("::")) + "." + Perl6ModuleFileType.INSTANCE.getDefaultExtension();
        new File(modulePath).getParentFile().mkdirs();
        List<String> code = new ArrayList<>(getModuleCodeByType(moduleType, moduleName, isUnitScoped));
        if (languageVersion != null)
            code.add(0, String.format("use v%s;", languageVersion));
        Perl6Utils.writeCodeToPath(Paths.get(modulePath), code);
        if (moduleType.equals("Monitor")) {
            metaData.addDepends("OO::Monitors");
        }
        if (moduleType.equals("Model")) {
            metaData.addDepends("Red");
        }
        return modulePath;
    }

    private static List<String> getModuleCodeByType(String type,
                                                    String name,
                                                    boolean isUnitScoped) {
        if (isUnitScoped) {
            String declText = String.format("unit %s %s;", type.toLowerCase(Locale.ENGLISH), name);
            return switch (type) {
                case "Class", "Role", "Grammar", "Module" -> Arrays.asList(declText, "");
                case "Monitor" -> Arrays.asList(
                    "use OO::Monitors;", "",
                    declText, "");
                case "Model" -> Arrays.asList(
                    "use Red;", "",
                    declText, "");
                default -> Collections.singletonList("");
            };
        }
        else {
            String declText = String.format("%s %s {", type.toLowerCase(Locale.ENGLISH), name);
            return switch (type) {
                case "Class", "Role", "Grammar", "Module" -> Arrays.asList(declText, "", "}");
                case "Monitor" -> Arrays.asList(
                    "use OO::Monitors;", "",
                    declText, "", "}");
                case "Model" -> Arrays.asList(
                    "use Red;", "",
                    declText, "", "}");
                default -> Collections.singletonList("");
            };
        }
    }

    public static String stubTest(Path testDirectoryPath,
                                  String fileName,
                                  List<String> imports,
                                  RakuLanguageVersion languageVersion) {
        Path testPath = testDirectoryPath.resolve(fileName);
        // If no extension, add default `.t`
        if (!fileName.contains("."))
            testPath = Paths.get(testDirectoryPath.toString(), fileName + "." + Perl6TestFileType.INSTANCE.getDefaultExtension());
        List<String> lines = new LinkedList<>();
        lines.add(String.format("use v%s;", languageVersion));
        imports.forEach(i -> lines.add(String.format("use %s;", i)));
        lines.addAll(Arrays.asList("use Test;", "", "done-testing;"));
        Perl6Utils.writeCodeToPath(testPath, lines);
        return testPath.toString();
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myModuleName != null && nameField != null)
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myModuleName));
    }
}
