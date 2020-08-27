package edument.perl6idea.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import edument.perl6idea.utils.Patterns;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Paths;

public class NewTestDialog extends DialogWrapper {
    private final String myParentPath;
    private JTextField testNameField;
    private JPanel myPanel;

    protected NewTestDialog(@Nullable Project project, String parentPath) {
        super(project, false);
        myParentPath = parentPath;
        init();
        setTitle("New Raku Script");
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return testNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String newScriptName = testNameField.getText();
        boolean isCorrectScriptName = newScriptName.matches(Patterns.TEST_PATTERN);
        if (!isCorrectScriptName)
            return new ValidationInfo("Incorrect test name (examples: `foo.rakutest`, `foo.t6`, `foo.t`)", testNameField);
        if (Paths.get(myParentPath, newScriptName).toFile().exists() ||
            Paths.get(myParentPath, newScriptName + ".t").toFile().exists())
            return new ValidationInfo("File " + newScriptName + " already exists", testNameField);
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myPanel;
    }

    public String getTestName() {
        return testNameField.getText();
    }
}
