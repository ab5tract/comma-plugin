package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Splitter;
import com.intellij.refactoring.ui.MethodSignatureComponent;
import com.intellij.refactoring.ui.NameSuggestionsField;
import com.intellij.refactoring.ui.RefactoringDialog;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Objects;
import java.util.StringJoiner;

public abstract class Perl6ExtractMethodDialog extends RefactoringDialog {
    public static final String[] SCOPE_OPTIONS = {"", "my", "our"};
    private JPanel myContentPane;
    private NameSuggestionsField myNameField;
    private JComboBox<String> myScopeField;
    private JTextField myReturnTypeField;
    private JBTable myParams;
    private MethodSignatureComponent mySignature;
    private String myCodeBlockType;
    private boolean myIsPrivate;
    private Perl6VariableData[] myInputVariables;

    protected Perl6ExtractMethodDialog(Project project, String title, Perl6CodeBlockType codeBlockType, Perl6VariableData[] myInputVariables) {
        super(project, true);
        mySignature = new MethodSignatureComponent("", project, Perl6ScriptFileType.INSTANCE);
        this.myInputVariables = myInputVariables;
        mySignature.setMinimumSize(new Dimension(400, 100));
        myNameField = new NameSuggestionsField(ArrayUtil.EMPTY_STRING_ARRAY, myProject);
        myScopeField = new ComboBox<>(SCOPE_OPTIONS);
        myReturnTypeField = new JTextField();
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

        // Scope piece
        final JPanel scopePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel scopeLabel = new JLabel("Scope:");
        scopePanel.add(scopeLabel, BorderLayout.NORTH);
        scopePanel.add(myScopeField, BorderLayout.SOUTH);
        scopeLabel.setLabelFor(myScopeField);
        myScopeField.addItemListener(e -> update());
        main.add(scopePanel, BorderLayout.WEST);

        // Name piece
        final JPanel namePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel nameLabel = new JLabel("Name:");
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(myNameField, BorderLayout.SOUTH);
        nameLabel.setLabelFor(myNameField);
        myNameField.addDataChangedListener(this::update);
        main.add(namePanel, BorderLayout.CENTER);

        // Return type piece
        final JPanel returnTypePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel returnTypeLabel = new JLabel("Returns:");
        returnTypePanel.add(returnTypeLabel, BorderLayout.NORTH);
        returnTypePanel.add(myReturnTypeField, BorderLayout.SOUTH);
        returnTypeLabel.setLabelFor(myReturnTypeField);
        myReturnTypeField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                update();
            }
        });
        main.add(returnTypePanel, BorderLayout.EAST);

        return main;
    }

    private void update() {
        updateSignature();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myContentPane = new JPanel(new BorderLayout());
        createParametersPanel();

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

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    private void updateSignature() {
        if (mySignature != null) {
            mySignature.setSignature(getSignature());
        }
    }

    private String getSignature() {
        String baseFormat = "%s %s%s(%s) {\n\n}";
        String scopedFormat = "%s %s";
        String base = String.format(baseFormat,
                             myCodeBlockType,
                             myIsPrivate ? "!" : "",
                             myNameField.getEnteredName(), prepareSignatureParameterBlock());
        Object item = myScopeField.getSelectedItem();
        return !Objects.equals(item, "") ? String.format(scopedFormat, item, base) : base;
    }

    private String prepareSignatureParameterBlock() {
        String retType = myReturnTypeField.getText();
        String base = ""; // parameters and return type
        if (myInputVariables.length != 0) {
            StringJoiner paramsJoiner = new StringJoiner(", ");
            for (Perl6VariableData var : myInputVariables) {
                paramsJoiner.add(var.getPresentation());
            }
            base += paramsJoiner.toString();
        }
        if (!retType.isEmpty())
            base += "--> " + retType;
        return base;
    }

    private void createParametersPanel() {
    }

    public String getScope() {
        return (String)myScopeField.getSelectedItem();
    }

    public String getName() {
        return myNameField.getEnteredName();
    }

    public Perl6VariableData[] getInputVariables() {
        return myInputVariables;
    }

    public String getReturnType() {
        return myReturnTypeField.getText();
    }
}
