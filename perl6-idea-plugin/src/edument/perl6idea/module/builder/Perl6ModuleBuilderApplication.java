package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class Perl6ModuleBuilderApplication implements Perl6ModuleBuilderGeneric {
    private String myModuleName;
    private String myEntryPointName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            Perl6MetaDataComponent metaData = model.getModule().getService(Perl6MetaDataComponent.class);
            VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
            Perl6ModuleBuilderModule.stubModule(metaData, path, myModuleName, true, false,
                                                sourceRoot == null ? null : sourceRoot.getParent(), "Empty", false);
        } else if (Objects.equals(directoryName.toString(), "bin")) {
            stubEntryPoint(path, myModuleName, myEntryPointName);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            Perl6ModuleBuilderModule.stubTest(path,
                     "00-sanity.t",
                         Collections.singletonList(myModuleName));
        }
    }

    @Override
    public void loadFromDialogData(Map<String, String> data) {
        myModuleName = data.get(Perl6ModuleWizardStep.MODULE_NAME);
        myEntryPointName = data.get(Perl6ModuleWizardStep.ENTRY_POINT_NAME);
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"bin", "lib", "t"};
    }

    private static void stubEntryPoint(Path moduleLibraryPath, String moduleName, String entryPoitnName) {
        Path entryPath = moduleLibraryPath.resolve(entryPoitnName);
        List<String> lines = Arrays.asList(
            "#!/usr/bin/env perl6",
            String.format("use %s;", moduleName)
        );
        Perl6Utils.writeCodeToPath(entryPath, lines);
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myModuleName != null && nameField != null)
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myModuleName));
    }
}
