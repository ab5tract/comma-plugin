package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import edument.perl6idea.utils.Patterns;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NewScriptDialog extends DialogWrapper {
    private JTextField scriptNameField;
    private JPanel myPanel;
    private JCheckBox myIsTemplated;

    protected NewScriptDialog(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        init();
        setTitle("New Perl 6 Script");
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return scriptNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        boolean isCorrectScriptName = scriptNameField.getText().matches(Patterns.SCRIPT_PATTERN);
        if (isCorrectScriptName)
            return null;
        return new ValidationInfo("Incorrect script name (examples: `main`, `main.p6`)", scriptNameField);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myPanel;
    }

    public String getScriptName() {
        return scriptNameField.getText();
    }

    public boolean shouldAddTemplate() {
        return myIsTemplated.isSelected();
    }
}
