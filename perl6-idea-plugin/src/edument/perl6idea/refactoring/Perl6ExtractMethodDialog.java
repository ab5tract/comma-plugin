package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.refactoring.ui.MethodSignatureComponent;
import com.intellij.refactoring.ui.NameSuggestionsField;
import com.intellij.refactoring.ui.RefactoringDialog;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class Perl6ExtractMethodDialog extends RefactoringDialog {
    private JPanel myContentPane;
    private NameSuggestionsField myNameField;
    private JTextField myReturnType;
    private JBTable myParams;
    private MethodSignatureComponent mySignature;
    private String myCodeBlockType;
    private boolean myIsPrivate;

    protected Perl6ExtractMethodDialog(Project project, String title, Perl6CodeBlockType codeBlockType) {
        super(project, true);
        mySignature = new MethodSignatureComponent("", project, Perl6ScriptFileType.INSTANCE);
        myNameField = new NameSuggestionsField(ArrayUtil.EMPTY_STRING_ARRAY, myProject);
        myReturnType = new JTextField();
        switch (codeBlockType) {
            case METHOD:
            case PRIVATEMETHOD:
                myCodeBlockType = "method";
                break;
            case ROUTINE:
                myCodeBlockType = "sub";
                break;
        }
        myIsPrivate = codeBlockType == Perl6CodeBlockType.PRIVATEMETHOD;
        setTitle(title);
        init();
    }

    @Nullable
    @Override
    protected JComponent createNorthPanel() {
        final JPanel main = new JPanel(new BorderLayout());
        final JPanel namePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel nameLabel = new JLabel();
        nameLabel.setText("Name:");
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(myNameField, BorderLayout.SOUTH);
        nameLabel.setLabelFor(myNameField);
        myNameField.addDataChangedListener(this::update);
        main.add(namePanel, BorderLayout.CENTER);
        return main;
    }

    private void update() {
        updateSignature();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myContentPane = new JPanel(new BorderLayout());
        createParameters();

        final Splitter splitter = new Splitter(true);
        splitter.setShowDividerIcon(false);
        splitter.setFirstComponent(myContentPane);

        JPanel secondPanel = new JPanel(new BorderLayout(0, 5));
        secondPanel.add(createSignaturePanel(), BorderLayout.CENTER);
        splitter.setSecondComponent(secondPanel);
        return splitter;
    }

    private JComponent createSignaturePanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(SeparatorFactory.createSeparator("Signature Preview", null), BorderLayout.NORTH);
        panel.add(mySignature, BorderLayout.CENTER);
        updateSignature();
        return panel;
    }

    private void updateSignature() {
        if (mySignature != null) {
            mySignature.setSignature(getSignature());
        }
    }

    private String getSignature() {
        String format = "%s %s%s(%s) {}";
        return String.format(format,
                             myCodeBlockType,
                             myIsPrivate ? "!" : "",
                             myNameField.getEnteredName(), "");
    }

    private void createParameters() {

    }

    public String getName() {
        return myNameField.getEnteredName();
    }

    @Override
    protected void doAction() { System.out.println("Action was called"); }
}
