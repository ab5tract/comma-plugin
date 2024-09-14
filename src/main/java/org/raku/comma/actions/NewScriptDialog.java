package org.raku.comma.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.raku.comma.utils.Patterns;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Paths;

public class NewScriptDialog extends DialogWrapper {
    private final String myParentPath;
    private JTextField scriptNameField;
    private JPanel myPanel;
    private JCheckBox myIsTemplated;

    protected NewScriptDialog(@Nullable Project project, String parentPath) {
        super(project, false);
        myParentPath = parentPath;
        init();
        setTitle("New Raku Script");
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return scriptNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String newScriptName = scriptNameField.getText();
        boolean isCorrectScriptName = newScriptName.matches(Patterns.SCRIPT_PATTERN);
        if (!isCorrectScriptName)
            return new ValidationInfo("Incorrect script name (examples: `main`, `main.raku`, `main.p6`)", scriptNameField);
        if (Paths.get(myParentPath, newScriptName).toFile().exists())
            return new ValidationInfo("File " + newScriptName + " already exists", scriptNameField);
        return null;
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
