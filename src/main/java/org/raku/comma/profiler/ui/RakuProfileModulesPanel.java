package org.raku.comma.profiler.ui;

import com.intellij.openapi.util.Pair;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.table.JBTable;
import org.raku.comma.profiler.model.RakuProfileData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RakuProfileModulesPanel extends JPanel {
    private final JBTable myModulesTable;
    private final TableCellRenderer myProfileNodeRenderer = new ColoredTableCellRenderer() {
        private final DecimalFormat myFormatter = new DecimalFormat("#,###");

        @Override
        protected void customizeCellRenderer(JTable table,
                                             @Nullable Object value,
                                             boolean selected,
                                             boolean hasFocus,
                                             int row,
                                             int column) {
            if (value != null)
                append(String.format("%s μs", myFormatter.format(value)));
        }
    };

    public RakuProfileModulesPanel(RakuProfileData data) {
        setLayout(new BorderLayout());
        myModulesTable = new JBTable();
        myModulesTable.setAutoCreateRowSorter(true);
        Map<String, Pair<Integer, Integer>> modulesData = calculateModulesData(data);
        myModulesTable.setModel(new ProfilerModulesTableModel(modulesData));
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        myModulesTable.getRowSorter().setSortKeys(sortKeys);
        myModulesTable.setDefaultRenderer(Integer.class, myProfileNodeRenderer);
        add(new JScrollPane(myModulesTable), BorderLayout.CENTER);
    }

    private static Map<String, Pair<Integer, Integer>> calculateModulesData(RakuProfileData profileData) {
        try {
            return profileData.getModuleNodes();
        }
        catch (SQLException throwables) {
            return new HashMap<>();
        }
    }

    private static class ProfilerModuleItem {
        public final String name;
        final float inclusive;
        final float exclusive;

        ProfilerModuleItem(String name, Integer inclusive, Integer exclusive) {
            this.name = name;
            this.inclusive = inclusive;
            this.exclusive = exclusive;
        }
    }

    private static class ProfilerModulesTableModel implements TableModel {
        private final List<ProfilerModuleItem> myData;

        ProfilerModulesTableModel(Map<String, Pair<Integer, Integer>> data) {
            myData = new ArrayList<>();
            for (Map.Entry<String, Pair<Integer, Integer>> entry : data.entrySet()) {
                myData.add(new ProfilerModuleItem(entry.getKey(), entry.getValue().first, entry.getValue().second));
            }
        }

        @Override
        public int getRowCount() {
            return myData.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Module";
            }
            return "Exclusive time";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return String.class;
            return Integer.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return myData.get(rowIndex).name;
            }
            return myData.get(rowIndex).exclusive;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }
}
