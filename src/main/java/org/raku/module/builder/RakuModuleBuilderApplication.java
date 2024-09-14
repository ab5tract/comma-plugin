package org.raku.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.language.RakuLanguageVersion;
import org.raku.metadata.RakuMetaDataComponent;
import org.raku.module.RakuModuleWizardStep;
import org.raku.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class RakuModuleBuilderApplication implements RakuModuleBuilderGeneric {
    private String myModuleName;
    private String myEntryPointName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model,
                                     Path path,
                                     RakuLanguageVersion languageVersion) {
        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            RakuMetaDataComponent metaData = model.getModule().getService(RakuMetaDataComponent.class);
            VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
            RakuModuleBuilderModule.stubModule(metaData, path, myModuleName, true, false,
                                                sourceRoot == null ? null : sourceRoot.getParent(), "Empty", false,
                                               languageVersion);
        } else if (Objects.equals(directoryName.toString(), "bin")) {
            stubEntryPoint(path, myModuleName, myEntryPointName, languageVersion);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            RakuModuleBuilderModule.stubTest(path,
                                             "basic.rakutest",
                                             Collections.singletonList(myModuleName), languageVersion);
        }
    }

    @Override
    public void loadFromDialogData(Map<String, String> data) {
        myModuleName = data.get(RakuModuleWizardStep.MODULE_NAME);
        myEntryPointName = data.get(RakuModuleWizardStep.ENTRY_POINT_NAME);
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"bin", "lib", "t"};
    }

    private static void stubEntryPoint(Path moduleLibraryPath,
                                       String moduleName,
                                       String entryPoitnName,
                                       RakuLanguageVersion languageVersion) {
        Path entryPath = moduleLibraryPath.resolve(entryPoitnName);
        List<String> lines = Arrays.asList(
            "#!/usr/bin/env raku",
            languageVersion != null ? String.format("use v%s;", languageVersion) : "",
            String.format("use %s;", moduleName)
        );
        RakuUtils.writeCodeToPath(entryPath, lines);
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myModuleName != null && nameField != null)
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myModuleName));
    }
}
