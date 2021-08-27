package edument.perl6idea.profiler.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.model.Perl6ProfileModelWithRatio;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

import static edument.perl6idea.profiler.ui.Perl6ProfileGCPanel.parseAndCompare;

public class Perl6ProfileAllocationsPanel extends JPanel {
    private final Perl6ProfileData myProfileData;
    private JPanel myPanel;
    private JBTable allocationsTable;
    private JBTable typeDetailsTable;
    private JSplitPane mySplitPane;
    private JBScrollPane allocationsTablePane;
    private JBScrollPane typeDetailsTablePane;

    public Perl6ProfileAllocationsPanel(Perl6ProfileData data) {
        mySplitPane.setResizeWeight(0.35);
        myProfileData = data;
        setup();
        setupSorter(allocationsTable, 1, 2, 3, 4);
        setupSorter(typeDetailsTable, 2, 3, 4, 5, 6, 7);
    }

    public void setupSorter(JTable table, int... columnsToParse) {
        table.setRowSorter(new TableRowSorter<>(table.getModel()) {
            @Override
            public Comparator<?> getComparator(int column) {
                if (ArrayUtils.contains(columnsToParse, column))
                    return (Comparator<Object>)(o1, o2) -> {
                        if (ArrayUtils.contains(columnsToParse, column)) {
                            return parseAndCompare((String)o1, (String)o2);
                        }
                        return 0;
                    };
                return Comparator.naturalOrder();
            }
        });
    }

    private void setup() {
        allocationsTablePane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder (), "Objects of Type Allocated", TitledBorder.LEFT, TitledBorder.TOP));
        typeDetailsTablePane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder (), "Allocation sites of type", TitledBorder.LEFT, TitledBorder.TOP));

        List<AllocationData> allocsData = myProfileData.getAllocatedTypes();
        allocationsTable.setModel(new Perl6ProfileAllocationsTableModel(allocsData));
        allocationsTable.getColumnModel().getColumn(4).setCellRenderer(new PercentageTableCellRenderer());

        allocationsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting())
                    return;
                updateAllocationData();
            }
        });
        updateAllocationData();
    }

    private void updateAllocationData() {
        int row = allocationsTable.getSelectedRow();
        if (row == -1)
            row = 0;
        int typeRow = allocationsTable.convertRowIndexToModel(row);
        if (typeRow >= 0) {
            Perl6ProfileAllocationsTableModel model = (Perl6ProfileAllocationsTableModel)allocationsTable.getModel();
            int typeId = model.getAllocationId(typeRow);
            List<AllocatedTypeDetails> list = myProfileData.getAllocatedTypeData(typeId);
            typeDetailsTable.setModel(new Perl6ProfileTypeDetailsTableModel(list));
            typeDetailsTable.getColumnModel().getColumn(4).setCellRenderer(new PercentageTableCellRenderer());
            typeDetailsTable.getColumnModel().getColumn(7).setCellRenderer(new PercentageTableCellRenderer());
            setupSorter(typeDetailsTable, 2, 3, 4, 5, 6, 7);
        }
    }

    public Component getPanel() {
        return myPanel;
    }

    private static class Perl6ProfileAllocationsTableModel implements TableModel, Perl6ProfileModelWithRatio {
        List<AllocationData> allocsData;

        Perl6ProfileAllocationsTableModel(List<AllocationData> allocsData) {
            this.allocsData = allocsData;
        }

        @Override
        public int getRowCount() {
            return allocsData.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return "Name";
                case 1: return "Count";
                case 2: return "Type size (bytes)";
                case 3: return "Total size (bytes)";
                default: return "Optimized Out";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AllocationData alloc = allocsData.get(rowIndex);
            DecimalFormat format = new DecimalFormat("###,###.###");

            switch (columnIndex) {
                case 0:
                    return alloc.name;
                case 1:
                    return format.format(alloc.count);
                case 2:
                    return String.format("%s", alloc.managed_size);
                case 3:
                    return String.format("%s", format.format(alloc.total));
                default:
                    return alloc.optimized;
            }
        }

        @Override
        public double getRatio(long value, int row, int column) {
            return (double)value / allocsData.get(row).count;
        }

        public int getAllocationId(int row) {
            return allocsData.get(row).type_id;
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

    private static class Perl6ProfileTypeDetailsTableModel implements TableModel, Perl6ProfileModelWithRatio {
        List<AllocatedTypeDetails> typeDetailsList;

        Perl6ProfileTypeDetailsTableModel(List<AllocatedTypeDetails> list) {
            typeDetailsList = list;
        }

        @Override
        public int getRowCount() {
            return typeDetailsList.size();
        }

        @Override
        public int getColumnCount() {
            return 8;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return "Name";
                case 1: return "Path";
                case 2: return "Sites";
                case 3: return "Entries";
                case 4: return "JIT";
                case 5: return "Size (bytes)";
                case 6: return "Count";
                default: return "Optimized Out";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DecimalFormat format = new DecimalFormat("###,###.###");
            if (rowIndex >= typeDetailsList.size())
                return null;
            AllocatedTypeDetails details = typeDetailsList.get(rowIndex);
            switch (columnIndex) {
                case 0: return details.name.isEmpty() ? "<anon>" : details.name;
                case 1: return details.path + ":" + details.line;
                case 2: return format.format(details.sites);
                case 3: return format.format(details.entries);
                case 4: return details.jit;
                case 5: return format.format(details.size);
                case 6: return format.format(details.count);
                default: return details.optimized;
            }
        }

        @Override
        public double getRatio(long value, int row, int column) {
            AllocatedTypeDetails details = typeDetailsList.get(row);
            return value / (double)details.entries;
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

    public static class AllocationData {
        private final int type_id;
        private final String name;
        private final long managed_size;
        private final long total;
        private final long count;
        private final long optimized;

        public AllocationData(int type_id, String name, long managed_size, long total, long count, long optimized) {
            this.type_id = type_id;
            this.name = name;
            this.managed_size = managed_size;
            this.total = total;
            this.count = count;
            this.optimized = optimized;
        }
    }

    public static class AllocatedTypeDetails {
        private final String name;
        private final String path;
        private final int line;
        private final int sites;
        private final long entries;
        private final long jit;
        private final int size;
        private final long count;
        private final long optimized;

        public AllocatedTypeDetails(String name, String path, int line, int sites, long entries,
                                    long jit, int size, long count, long optimized) {
            this.name = name;
            this.path = path;
            this.line = line;
            this.sites = sites;
            this.entries = entries;
            this.jit = jit;
            this.size = size;
            this.count = count;
            this.optimized = optimized;
        }
    }
}
