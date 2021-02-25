package edument.perl6idea.profiler.ui;

import com.intellij.ui.table.JBTable;
import edument.perl6idea.profiler.model.Perl6ProfileData;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class Perl6ProfileAllocationsPanel extends JPanel {
    private final Perl6ProfileData myProfileData;
    private JPanel myPanel;
    private JBTable allocationsTable;
    private JBTable typeDetailsTable;
    private JSplitPane mySplitPane;

    public Perl6ProfileAllocationsPanel(Perl6ProfileData data) {
        mySplitPane.setResizeWeight(0.4);
        myProfileData = data;
        setupTables();
    }

    private void setupTables() {
        List<AllocationData> allocsData = myProfileData.getAllocatedTypes();
        allocationsTable.setModel(new Perl6ProfileAllocationsTableModel(allocsData));
        allocationsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
                    updateAllocationData();
            }
        });
        allocationsTable.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON1)
                        return;
                    int index = allocationsTable.rowAtPoint(e.getPoint());
                    if (index < 0)
                        return;
                    int row = allocationsTable.convertRowIndexToModel(index);
                    // goToCallAtRow(row);
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
        }
    }

    public Component getPanel() {
        return myPanel;
    }

    private static class Perl6ProfileAllocationsTableModel implements TableModel {
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
                case 1: return "Type size";
                case 2: return "Total size";
                case 3: return "Count";
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
            DecimalFormat format = new DecimalFormat("###,###");

            switch (columnIndex) {
                case 0:
                    return alloc.name;
                case 1:
                    return String.format("%s bytes", alloc.managed_size);
                case 2:
                    return String.format("%s bytes", format.format(alloc.total));
                case 3:
                    return format.format(alloc.count);
                default:
                    return format.format(alloc.optimized);
            }
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

    private static class Perl6ProfileTypeDetailsTableModel implements TableModel {
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
            AllocatedTypeDetails details = typeDetailsList.get(rowIndex);
            switch (columnIndex) {
                case 0: return details.name.isEmpty() ? "<anon>" : details.name;
                case 1: return details.path + ":" + details.line;
                case 2: return format.format(details.sites);
                case 3: return format.format(details.entries);
                case 4: return String.format("%s (%s%%)", format.format(details.jit), format.format(details.jit / (float)details.entries * 100f));
                case 5: return format.format(details.size);
                case 6: return format.format(details.count);
                default:
            }
            return null;
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
        private final long optimized = -1;

        public AllocatedTypeDetails(String name, String path, int line, int sites, long entries,
                                    long jit, int size, long count) {
            this.name = name;
            this.path = path;
            this.line = line;
            this.sites = sites;
            this.entries = entries;
            this.jit = jit;
            this.size = size;
            this.count = count;
            //this.optimized = optimized;
        }
    }
}
