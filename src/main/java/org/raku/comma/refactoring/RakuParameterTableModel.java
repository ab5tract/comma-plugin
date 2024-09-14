package org.raku.comma.refactoring;

import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.EditableModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public abstract class RakuParameterTableModel extends AbstractTableModel implements EditableModel {
    private final String[] columns = {"Name", "Type", "Pass as Parameter", "Available Lexically"};
    private final RakuVariableData[] myVars;
    protected final JTable myTable;

    public RakuParameterTableModel(RakuVariableData[] variableData, JTable table) {
        super();
        myVars = variableData;
        myTable = table;
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
        return columnIndex != RakuExtractBlockDialog.LEXICAL_SCOPE_COLUMN_INDEX;
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: {
                myVars[rowIndex].parameterName = (String)newValue;
                break;
            }
            case 1: {
                myVars[rowIndex].type = (String)newValue;
                break;
            }
            case RakuExtractBlockDialog.PASSED_AS_PARAMETER_COLUMN_INDEX:
            case RakuExtractBlockDialog.LEXICAL_SCOPE_COLUMN_INDEX: {
                myVars[rowIndex].isPassed = (boolean)newValue;
                break;
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
        updateOwner();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RakuVariableData var = myVars[rowIndex];
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
        if (columnIndex == 2) {
            return Boolean.class;
        }
        return String.class;
    }

    @Override
    public void addRow() {
        throw new IncorrectOperationException("Cannot add a row");
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        if (!canExchangeRows(oldIndex, newIndex)) return;

        final RakuVariableData targetVar = myVars[newIndex];
        myVars[newIndex] = myVars[oldIndex];
        myVars[oldIndex] = targetVar;

        myTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
        updateOwner();
    }

    public abstract void updateOwner();

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        if (oldIndex < 0 || oldIndex >= myVars.length) return false;
        if (newIndex < 0 || newIndex >= myVars.length) return false;
        return true;
    }

    @Override
    public void removeRow(int idx) {
        throw new IncorrectOperationException("Cannot remove a row");
    }
}
