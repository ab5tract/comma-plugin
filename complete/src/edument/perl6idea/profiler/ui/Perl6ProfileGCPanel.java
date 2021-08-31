package edument.perl6idea.profiler.ui;

import com.intellij.ui.table.JBTable;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.model.Perl6ProfileModelWithRatio;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;

public class Perl6ProfileGCPanel extends JPanel {
    public Perl6ProfileGCPanel(Perl6ProfileData data) {
        super(new BorderLayout());
        JTable gcTable = new JBTable();
        GCTableModel tableModel = new GCTableModel(data);
        gcTable.setModel(tableModel);
        gcTable.getColumnModel().getColumn(0).setMaxWidth(50);
        gcTable.getColumnModel().getColumn(5).setCellRenderer(new PercentageTableCellRenderer());
        gcTable.getColumnModel().getColumn(6).setCellRenderer(new PercentageTableCellRenderer());
        gcTable.getColumnModel().getColumn(7).setCellRenderer(new PercentageTableCellRenderer());
        setupSorter(gcTable, tableModel);
        add(new JScrollPane(gcTable), BorderLayout.CENTER);
    }

    private static void setupSorter(JTable gcTable, GCTableModel tableModel) {
        gcTable.setRowSorter(new TableRowSorter<>(tableModel) {
            @Override
            public Comparator<?> getComparator(int column) {
                switch (column) {
                    case 0:
                    case 1:
                    case 5:
                    case 6:
                    case 7:
                        return Comparator.naturalOrder();
                    case 8:
                        return null;
                    default:
                        return (Comparator<Object>)(o1, o2) -> {
                            if (column >= 2 && column <= 6) {
                                if (o1.equals("-"))
                                    return -1;
                                if (o2.equals("-"))
                                    return 1;
                                return parseAndCompare((String)o1, (String)o2);
                            }
                            return 0;
                        };
                }
            }
        });
    }

    static int parseAndCompare(String o1, String o2) {
        try {
            Number o1Value = new DecimalFormat("###,###.###").parse(o1);
            Number o2Value = new DecimalFormat("###,###.###").parse(o2);
            return Double.compare(o1Value.doubleValue(), o2Value.doubleValue());
        }
        catch (ParseException e) {
            return -1;
        }
    }

    private static class GCTableModel implements TableModel, Perl6ProfileModelWithRatio {
        List<GCData> gcs;

        private GCTableModel(Perl6ProfileData data) {
            gcs = data.getGC();
        }

        @Override
        public int getRowCount() {
            return gcs.size();
        }

        @Override
        public int getColumnCount() {
            return 9;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return "N";
                case 1: return "Full";
                case 2: return "Time spent";
                case 3: return "Start time";
                case 4: return "Since previous";
                case 5: return "Promoted bytes";
                case 6: return "Retained bytes";
                case 7: return "Cleared bytes";
                default: return "Threads";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return Integer.class;
            else if (columnIndex == 1)
                return Boolean.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            GCData prev = null;
            if (rowIndex != 0)
                prev = gcs.get(rowIndex - 1);
            GCData item = gcs.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex;
                case 1:
                    return item.full;
                case 2:
                    return new DecimalFormat("###.##").format(item.time / 1000f) + "ms";
                case 3:
                    return String.format("%sms", new DecimalFormat("###.##").format(item.startTime / 1000f));
                case 4: {
                    if (prev != null)
                        return String.format("%sms", new DecimalFormat("###.##").format((item.startTime - prev.time - prev.startTime) / 1000f));
                    else
                        return "-";
                }
                case 5:
                    return item.promotedBytes;
                case 6:
                    return item.retainedBytes;
                case 7:
                    return item.clearedBytes;
                default:
                    return item.threads;
            }
        }

        @Override
        public double getRatio(long value, int row, int column) {
            GCData item = gcs.get(row);
            long totalBytes = item.promotedBytes + item.retainedBytes + item.clearedBytes;
            if (totalBytes == 0) {
                return 0;
            }
            return (double)value / totalBytes;
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

    public static class GCData {
        private final long time;
        private final long startTime;
        private final long promotedBytes;
        private final long retainedBytes;
        private final long clearedBytes;
        private final String threads;
        private final boolean full;

        public GCData(long time, long startTime,
                      long promotedBytes, long retainedBytes,
                      long clearedBytes, String threads, boolean full) {
            this.time = time;
            this.startTime = startTime;
            this.promotedBytes = promotedBytes;
            this.retainedBytes = retainedBytes;
            this.clearedBytes = clearedBytes;
            this.threads = threads;
            this.full = full;
        }
    }
}
