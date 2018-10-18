package edument.perl6idea.refactoring.introduce;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorComboBoxEditor;
import com.intellij.ui.EditorComboBoxRenderer;
import com.intellij.ui.StringComboboxEditor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class Perl6IntroduceDialog extends DialogWrapper {
    private ComboBox myNameComboBox;
    private Project myProject;
    private JButton myCreateButton;
    private JPanel myContentPane;

    public Perl6IntroduceDialog(Project project,
                                String title,
                                IntroduceValidator validator,
                                String id,
                                IntroduceOperation operation) {
        super(project, true);
        myProject = project;

        setTitle(title);
        setUpNameComboBox(operation.getSuggestedNames());
        init();
    }

    private void setUpNameComboBox(Collection<String> names) {
        EditorComboBoxEditor comboBoxEditor = new StringComboboxEditor(myProject, Perl6ScriptFileType.INSTANCE, myNameComboBox);
        myNameComboBox.setEditor(comboBoxEditor);
        myNameComboBox.setRenderer(new EditorComboBoxRenderer(comboBoxEditor));
        myNameComboBox.setEditable(true);
        myNameComboBox.setMaximumRowCount(8);

        for (String possibleName : names) {
            myNameComboBox.addItem(possibleName);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myContentPane;
    }

    public boolean isModified(Perl6IntroduceDialog data) {
        return false;
    }

    public String getName() {
        Object item = myNameComboBox.getEditor().getItem();
        if (item instanceof String && ((String)item).length() > 0) {
            return ((String)item).trim();
        }
        return null;
    }
}
