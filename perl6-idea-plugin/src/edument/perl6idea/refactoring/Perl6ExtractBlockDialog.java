package edument.perl6idea.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.refactoring.ui.MethodSignatureComponent;
import com.intellij.refactoring.ui.NameSuggestionsField;
import com.intellij.refactoring.ui.RefactoringDialog;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.SeparatorFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public abstract class Perl6ExtractBlockDialog extends RefactoringDialog {
    public static final String[] SCOPE_OPTIONS = {"", "my", "our"};
    public static final String[] KIND_OPTIONS = {"", "proto", "multi", "only"};
    public static final int LEXICAL_SCOPE_COLUMN_INDEX = 3;
    public static final int PASSED_AS_PARAMETER_COLUMN_INDEX = 2;
    private NameSuggestionsField myNameField;
    private JComboBox<String> myScopeField;
    private JComboBox<String> myKindField;
    private JTextField myReturnTypeField;
    private MethodSignatureComponent mySignature;
    private String myCodeBlockType;
    private boolean myIsPrivate;
    private Perl6VariableData[] myInputVariables;

    protected Perl6ExtractBlockDialog(Project project, String title, Perl6CodeBlockType codeBlockType, Perl6VariableData[] myInputVariables) {
        super(project, true);
        this.myInputVariables = myInputVariables;
        mySignature = new MethodSignatureComponent("", project, Perl6ScriptFileType.INSTANCE);
        mySignature.setMinimumSize(new Dimension(500, 80));
        myNameField = new NameSuggestionsField(ArrayUtil.EMPTY_STRING_ARRAY, myProject);
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
        myIsPrivate = codeBlockType == Perl6CodeBlockType.PRIVATEMETHOD;
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
        myNameField.addDataChangedListener(this::update);
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
        final JPanel centerPanel = new JPanel(new MigLayout("wrap 1"));

        final JPanel parametersTablePanel = new JPanel(new BorderLayout());
        parametersTablePanel.add(SeparatorFactory.createSeparator("Parameters", null), BorderLayout.CENTER);
        parametersTablePanel.add(createParametersPanel(), BorderLayout.CENTER);
        centerPanel.add(parametersTablePanel, "align left");

        final JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.add(SeparatorFactory.createSeparator("Signature Preview", null), BorderLayout.CENTER);
        signaturePanel.add(mySignature, BorderLayout.CENTER);
        centerPanel.add(signaturePanel, "align left");

        return centerPanel;
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
        JTable table = new JBTable(new Perl6ParameterTableModel(myInputVariables));
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
        JScrollPane scrollPane = new JBScrollPane(table);
        table.setFillsViewportHeight(true);
        return scrollPane;
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
        return myNameField.getEnteredName();
    }

    public Perl6VariableData[] getInputVariables() {
        return myInputVariables;
    }

    public String getReturnType() {
        return myReturnTypeField.getText();
    }

    private class Perl6ParameterTableModel extends AbstractTableModel {
        String[] columns = {"Name", "Type", "Pass as Parameter", "Available Lexically"};
        private final Perl6VariableData[] myVars;

        public Perl6ParameterTableModel(Perl6VariableData[] variableData) {
            super();
            myVars = variableData;
        }

        @Override
        public int getRowCount() {
            return myVars.length;
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != LEXICAL_SCOPE_COLUMN_INDEX;
        }

        @Override
        public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                case 1: {
                    myVars[rowIndex].parameterName = (String)newValue;
                    break;
                }
                case PASSED_AS_PARAMETER_COLUMN_INDEX:
                case LEXICAL_SCOPE_COLUMN_INDEX: {
                    myVars[rowIndex].isPassed = (boolean)newValue;
                    break;
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
            update();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Perl6VariableData var = myVars[rowIndex];
            switch (columnIndex) {
                case 0: return var.parameterName;
                case 1: return var.type;
                case 2: return var.isPassed;
                default: return var.getLexicalState();
            }
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                case 1: return String.class;
                case 2: return Boolean.class;
                default: return String.class;
            }
        }
    }
}
