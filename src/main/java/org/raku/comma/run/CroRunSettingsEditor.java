package org.raku.comma.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.raku.comma.cro.run.RakuCroRunConfigurationBase;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CroRunSettingsEditor extends RakuRunSettingsEditor {
    private JCheckBox devModeEnabled;

    public CroRunSettingsEditor(Project project) {
        super(project);
    }

    @Override
    protected void resetEditorFrom(@NotNull RakuRunConfiguration conf) {
        super.resetEditorFrom(conf);
        if (conf instanceof RakuCroRunConfigurationBase) {
            devModeEnabled.setSelected(((RakuCroRunConfigurationBase)conf).getCroDevMode());
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
    protected void applyEditorTo(@NotNull RakuRunConfiguration conf) throws ConfigurationException {
        super.applyEditorTo(conf);
        if (conf instanceof RakuCroRunConfigurationBase) {
            ((RakuCroRunConfigurationBase)conf).setCroDevMode(devModeEnabled.isSelected());
        }
    }
}
