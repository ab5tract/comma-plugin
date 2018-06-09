package edument.perl6idea.module;

import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModuleMetaEditor implements ModuleConfigurationEditor {
    public ModuleMetaEditor(ModuleConfigurationState state) {}

    @Nls
    @Override
    public String getDisplayName() {
        return "META6 editor";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return new JLabel("Fields are here");
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
