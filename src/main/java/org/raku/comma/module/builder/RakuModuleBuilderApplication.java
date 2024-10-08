package org.raku.comma.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.language.RakuLanguageVersion;
import org.raku.comma.utils.RakuUtils;
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
        switch (directoryName.toString()) {
            case "lib" -> {
                VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
                RakuModuleBuilderModule.stubModule(model.getProject(),
                                                   path,
                                                   myModuleName,
                                                   true,
                                                   false,
                                                   sourceRoot == null
                                                       ? null
                                                       : sourceRoot.getParent(),
                                                   "Empty",
                                                   false,
                                                   languageVersion);
            }
            case "bin" -> stubEntryPoint(path, myModuleName, myEntryPointName, languageVersion);
            case "t" -> RakuModuleBuilderModule.stubTest(path,
                                                         "basic.rakutest",
                                                         Collections.singletonList(myModuleName), languageVersion);
        }
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"bin", "lib", "t"};
    }

    @Override
    public void setName(@NotNull String name) {
        myModuleName = name;
    }

    @Override
    public void setEntryPoint(@NotNull String entryPointName) {
        myEntryPointName = entryPointName;
    }

    private static void stubEntryPoint(Path moduleLibraryPath,
                                       String moduleName,
                                       String entryPointName,
                                       RakuLanguageVersion languageVersion) {
        Path entryPath = moduleLibraryPath.resolve(entryPointName);
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
