package edument.perl6idea.refactoring.helpers;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorComboBoxEditor;
import com.intellij.ui.EditorComboBoxRenderer;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.StringComboboxEditor;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.refactoring.RakuNameValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

public class Perl6IntroduceDialog extends DialogWrapper {
    private final String myHelpId;
    private final RakuNameValidator myValidator;
    private ComboBox<String> myNameComboBox;
    private final Project myProject;
    private JPanel myContentPane;

    public Perl6IntroduceDialog(Project project,
                                String title,
                                RakuNameValidator validator,
                                String id,
                                Collection<String> names) {
        super(project, true);
        myProject = project;
        myHelpId = id;
        myValidator = validator;
        setTitle(title);
        init();
        setUpNameComboBox(names);
        updateControls();
    }

    @Nullable
    @Override
    protected String getHelpId() {
        return myHelpId;
    }

    private void setUpNameComboBox(Collection<String> names) {
        EditorComboBoxEditor comboBoxEditor = new StringComboboxEditor(myProject, Perl6ScriptFileType.INSTANCE, myNameComboBox);
        myNameComboBox.setEditor(comboBoxEditor);
        myNameComboBox.setRenderer(new EditorComboBoxRenderer(comboBoxEditor));
        myNameComboBox.setEditable(true);
        myNameComboBox.setMaximumRowCount(8);
        myNameComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateControls();
            }
        });
        ((EditorTextField)myNameComboBox.getEditor().getEditorComponent()).addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                updateControls();
            }
        });

        myNameComboBox.requestFocusInWindow();

        for (String possibleName : names) {
            myNameComboBox.addItem(possibleName);
        }
    }

    private void updateControls() {
        boolean nameValid = myValidator.isNameValid(getName());
        setOKActionEnabled(nameValid);
        setErrorText(!nameValid ? "Name is invalid" : null, myNameComboBox);
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
