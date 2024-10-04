package org.raku.comma.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import org.raku.comma.language.RakuLanguageVersion;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class RakuModuleBuilderScript implements RakuModuleBuilderGeneric {
    private String myScriptName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model,
                                     Path path,
                                     RakuLanguageVersion languageVersion)
    {
        stubScript(path, myScriptName, true, languageVersion);
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{""};
    }

    public static String stubScript(Path moduleLibraryPath,
                                    String scriptName,
                                    boolean shouldFill,
                                    RakuLanguageVersion languageVersion)
    {
        List<String> lines = new ArrayList<>(Collections.singletonList("#!/usr/bin/env raku"));
        if (languageVersion != null) {
            lines.add(String.format("use v%s;", languageVersion));
        }
        if (shouldFill) {
            lines.addAll(Arrays.asList("", "", "sub MAIN() { }"));
        }
        Path path = moduleLibraryPath.resolve(scriptName);
        RakuUtils.writeCodeToPath(path, lines);
        return path.toString();
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myScriptName != null && nameField != null) {
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myScriptName));
        }
    }

    @Override
    public void setName(@NotNull String name) {
        myScriptName = name;
    }
}
