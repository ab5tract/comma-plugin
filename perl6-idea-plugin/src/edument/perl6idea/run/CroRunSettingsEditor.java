package edument.perl6idea.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import edument.perl6idea.cro.run.Perl6CroRunConfigurationBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CroRunSettingsEditor extends Perl6RunSettingsEditor {
    private JCheckBox devModeEnabled;

    public CroRunSettingsEditor(Project project) {
        super(project);
    }

    @Override
    protected void resetEditorFrom(@NotNull Perl6RunConfiguration conf) {
        super.resetEditorFrom(conf);
        if (conf instanceof Perl6CroRunConfigurationBase) {
            devModeEnabled.setSelected(((Perl6CroRunConfigurationBase)conf).getCroDevMode());
        }
    }

    @Override
    protected @NotNull JComponent getDebugTab() {
        JComponent tab = super.getDebugTab();
        devModeEnabled = new JCheckBox("Development Mode");
        devModeEnabled.setToolTipText("Turns on development features of Cro, such as template reload.");
        tab.add(devModeEnabled);
        return tab;
    }

    @Override
    protected void applyEditorTo(@NotNull Perl6RunConfiguration conf) throws ConfigurationException {
        super.applyEditorTo(conf);
        if (conf instanceof Perl6CroRunConfigurationBase) {
            ((Perl6CroRunConfigurationBase)conf).setCroDevMode(devModeEnabled.isSelected());
        }
    }
}
