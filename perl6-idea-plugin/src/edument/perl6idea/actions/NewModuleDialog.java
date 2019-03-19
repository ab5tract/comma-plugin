package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import edument.perl6idea.utils.Patterns;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NewModuleDialog extends DialogWrapper {
    private JTextField moduleNameField;
    private JComboBox<String> moduleTypeComboBox;
    private JPanel myPanel;
    private JCheckBox myIsUnitScopedCheckBox;

    protected NewModuleDialog(@Nullable Project project,
                              boolean canBeParent,
                              String prefix) {
        super(project, canBeParent);
        init();
        setTitle("New Perl 6 Module");
        moduleNameField.setText(prefix);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return moduleNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        boolean isCorrectModuleName = moduleNameField.getText().matches(Patterns.MODULE_PATTERN);
        if (isCorrectModuleName)
            return null;
        return new ValidationInfo("Incorrect module name (examples: `Module`, `Foo::Bar`)", moduleNameField);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        moduleTypeComboBox.addItem("Empty");
        moduleTypeComboBox.addItem("Class");
        moduleTypeComboBox.addItem("Role");
        moduleTypeComboBox.addItem("Grammar");
        moduleTypeComboBox.addItem("Monitor");
        moduleTypeComboBox.addItem("Module");
        return myPanel;
    }

    public String getModuleName() {
        return moduleNameField.getText();
    }

    public String getModuleType() {
        return (String)moduleTypeComboBox.getSelectedItem();
    }

    public boolean isUnitModule() {
        return myIsUnitScopedCheckBox.isSelected();
    }
}
