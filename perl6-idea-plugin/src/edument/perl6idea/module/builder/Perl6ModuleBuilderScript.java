package edument.perl6idea.module.builder;

import com.intellij.ide.util.projectWizard.ModuleNameLocationSettings;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.module.Perl6ModuleWizardStep;
import edument.perl6idea.utils.Perl6Utils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class Perl6ModuleBuilderScript implements Perl6ModuleBuilderGeneric {
    private String myScriptName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model, Path path) {
        stubScript(path, myScriptName, true);
    }

    @Override
    public void loadFromDialogData(Map<String, String> formData) {
        myScriptName = formData.get(Perl6ModuleWizardStep.SCRIPT_NAME);
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{""};
    }

    public static String stubScript(Path moduleLibraryPath, String scriptName, boolean shouldFill) {
        if (!scriptName.endsWith(".pl6") && !scriptName.endsWith(Perl6ScriptFileType.INSTANCE.getDefaultExtension()))
            scriptName += "." + Perl6ScriptFileType.INSTANCE.getDefaultExtension();
        List<String> lines = new ArrayList<>(Collections.singletonList("#!/usr/bin/env perl6"));
        if (shouldFill)
            lines.addAll(Arrays.asList("", "", "sub MAIN() { }"));
        Path path = moduleLibraryPath.resolve(scriptName);
        Perl6Utils.writeCodeToPath(path, lines);
        return path.toString();
    }

    @Override
    public void modifySettingsStep(SettingsStep step) {
        final ModuleNameLocationSettings nameField = step.getModuleNameLocationSettings();
        if (myScriptName != null && nameField != null)
            nameField.setModuleName(StringUtil.sanitizeJavaIdentifier(myScriptName));
    }
}
