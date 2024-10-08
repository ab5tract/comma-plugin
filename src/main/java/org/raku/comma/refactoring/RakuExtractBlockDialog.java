package org.raku.comma.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.refactoring.ui.MethodSignatureComponent;
import com.intellij.refactoring.ui.RefactoringDialog;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.table.JBTable;
import org.raku.comma.filetypes.RakuScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public abstract class RakuExtractBlockDialog extends RefactoringDialog {
    public static final String[] SCOPE_OPTIONS = {"", "my", "our"};
    public static final String[] KIND_OPTIONS = {"", "proto", "multi", "only"};
    public static final int LEXICAL_SCOPE_COLUMN_INDEX = 3;
    public static final int PASSED_AS_PARAMETER_COLUMN_INDEX = 2;
    private final JTextField myNameField;
    private final JComboBox<String> myScopeField;
    private final JComboBox<String> myKindField;
    private final JTextField myReturnTypeField;
    private final MethodSignatureComponent mySignature;
    private String myCodeBlockType;
    private final boolean myIsPrivate;
    private final RakuVariableData[] myInputVariables;

    protected RakuExtractBlockDialog(Project project, String title, RakuCodeBlockType codeBlockType, RakuVariableData[] myInputVariables) {
        super(project, true);
        this.myInputVariables = myInputVariables;
        mySignature = new MethodSignatureComponent("", project, RakuScriptFileType.INSTANCE);
        mySignature.setMinimumSize(new Dimension(500, 80));
        myNameField = new JTextField();
        myScopeField = new ComboBox<>(SCOPE_OPTIONS);
        myKindField = new ComboBox<>(KIND_OPTIONS);
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
        myIsPrivate = codeBlockType == RakuCodeBlockType.PRIVATEMETHOD;
        setTitle(title);
        init();
        update();
    }

    @Nullable
    @Override
    protected JComponent createNorthPanel() {
        final JPanel northPanel = new JPanel(new BorderLayout());

        // Scope piece
        final JPanel scopePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel scopeLabel = new JLabel("Scope:");
        scopePanel.add(scopeLabel, BorderLayout.NORTH);
        scopePanel.add(myScopeField, BorderLayout.SOUTH);
        scopeLabel.setLabelFor(myScopeField);
        myScopeField.addItemListener(e -> update());
        northPanel.add(scopePanel, BorderLayout.WEST);

        // Kind piece
        final JPanel kindPanel = new JPanel(new BorderLayout(0, 2));
        final JLabel kindLabel = new JLabel("Kind:");
        kindPanel.add(kindLabel, BorderLayout.NORTH);
        kindPanel.add(myKindField, BorderLayout.SOUTH);
        kindLabel.setLabelFor(myKindField);
        myKindField.addItemListener(e -> update());

        final JPanel westPanel = new JPanel(new HorizontalLayout(2));
        westPanel.add(scopePanel);
        westPanel.add(kindPanel);
        northPanel.add(westPanel, BorderLayout.WEST);

        // Name piece
        final JPanel namePanel = new JPanel(new BorderLayout(0, 2));
        final JLabel nameLabel = new JLabel("Name:");
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(myNameField, BorderLayout.SOUTH);
        nameLabel.setLabelFor(myNameField);
        myNameField.requestFocus();
        myNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                update();
            }
        });
        northPanel.add(namePanel, BorderLayout.CENTER);

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
        northPanel.add(returnTypePanel, BorderLayout.EAST);

        return northPanel;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        final JPanel centerPanel = new JPanel(new BorderLayout());

        final JPanel parametersTablePanel = new JPanel(new BorderLayout());
        parametersTablePanel.add(SeparatorFactory.createSeparator("Parameters", null), BorderLayout.NORTH);
        parametersTablePanel.add(createParametersPanel(), BorderLayout.CENTER);
        centerPanel.add(parametersTablePanel, BorderLayout.CENTER);

        final JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.add(SeparatorFactory.createSeparator("Signature Preview", null), BorderLayout.NORTH);
        signaturePanel.add(mySignature, BorderLayout.CENTER);
        centerPanel.add(signaturePanel, BorderLayout.SOUTH);

        return centerPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myNameField;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (getName() != null && !getName().isEmpty()) {
            return null;
        } else {
            return new ValidationInfo("Name is required", myNameField);
        }
    }

    private void update() {
        updateSignature();
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
        String baseFormat = "%s%s %s(%s) {\n\n}";
        String prefix = String.format("%s%s", getScope(), getKind());
        return String.format(baseFormat, prefix,
                             myCodeBlockType,
                             (myIsPrivate ? "!" : "") + getName(),
                             getSignatureParameterBlock());
    }

    private String getSignatureParameterBlock() {
        String retType = getReturnType();
        String base = ""; // parameters and return type
        if (myInputVariables.length != 0) {
            base += NewCodeBlockData.formSignature(myInputVariables, false);
        }
        if (!retType.isEmpty())
            base += " --> " + retType;
        return base;
    }

    private JComponent createParametersPanel() {
        JTable table = new JBTable();
        RakuParameterTableModel parameterTableModel = new RakuParameterTableModel(myInputVariables, table) {
            @Override
            public void updateOwner() {
                update();
            }
        };
        table.setModel(parameterTableModel);
        table.getColumnModel().getColumn(LEXICAL_SCOPE_COLUMN_INDEX).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == LEXICAL_SCOPE_COLUMN_INDEX && "NO".equals(value)) {
                    if (!(boolean)table.getModel().getValueAt(row, PASSED_AS_PARAMETER_COLUMN_INDEX))
                        comp.setForeground(JBColor.RED);
                } else {
                    comp.setForeground(JBColor.BLACK);
                }
                return comp;
            }
        });
        table.setFillsViewportHeight(true);
        return ToolbarDecorator.createDecorator(table).disableAddAction().disableRemoveAction().createPanel();
    }

    public String getScope() {
        String scope = (String)myScopeField.getSelectedItem();
        return scope == null || scope.isEmpty() ? "" : scope + " ";
    }

    public String getKind() {
        String kind = (String)myKindField.getSelectedItem();
        return kind == null || kind.isEmpty() ? "" : kind + " ";
    }

    public String getName() {
        return myNameField.getText();
    }

    public RakuVariableData[] getInputVariables() {
        return myInputVariables;
    }

    public String getReturnType() {
        return myReturnTypeField.getText();
    }
}
