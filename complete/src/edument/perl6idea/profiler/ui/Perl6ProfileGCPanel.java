package edument.perl6idea.profiler.ui;

import com.intellij.ui.table.JBTable;
import edument.perl6idea.profiler.model.Perl6ProfileData;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class Perl6ProfileGCPanel extends JPanel {
    public Perl6ProfileGCPanel(Perl6ProfileData data) {
        super(new BorderLayout());
        JTable gcTable = new JBTable();
        gcTable.setModel(new GCTableModel(data));
        add(new JScrollPane(gcTable), BorderLayout.CENTER);
    }

    private static class GCTableModel implements TableModel {
        List<GCData> gcs;

        public GCTableModel(Perl6ProfileData data) {
            gcs = data.getGC();
        }

        @Override
        public int getRowCount() {
            return gcs.size();
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0: return "ID and threads";
                case 1: return "Time spent";
                case 2: return "Start time";
                case 3: return "Promoted bytes";
                case 4: return "Retained bytes";
                default: return "Cleared bytes";
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
            GCData prev = null;
            if (rowIndex != 0)
                prev = gcs.get(rowIndex - 1);
            GCData item = gcs.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return formatGCTitle(item);
                case 1:
                    return formatGCTime(item.time);
                case 2:
                    return formatStartupTime(item.startTime, prev);
                case 3:
                    return formatBytes(item, item.promotedBytes);
                case 4:
                    return formatBytes(item, item.retainedBytes);
                default:
                    return formatBytes(item, item.clearedBytes);
            }
        }

        private static String formatGCTitle(GCData item) {
            return String.format("№ %s, threads: %s", item.id, item.threads);
        }

        private static String formatGCTime(long time) {
            String pattern = "###.##";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            double formattedTime = time / 1000f;
            return String.format("%sms spent", decimalFormat.format(formattedTime));
        }

        private static String formatStartupTime(long startTime, GCData prev) {
            DecimalFormat decimalFormat = new DecimalFormat("###.##");

            if (prev == null) {
                return String.format("%sms", decimalFormat.format(startTime / 1000f));
            }
            else {
                return String.format("%sms (%sms after previous)", decimalFormat.format(startTime / 1000f),
                                     decimalFormat.format((startTime - prev.time - prev.startTime) / 1000f));
            }
        }

        private static String formatBytes(GCData item, long bytes) {
            long totalBytes = item.promotedBytes + item.retainedBytes + item.clearedBytes;
            if (totalBytes == 0) {
                return "0%";
            }
            double percent = (double)bytes / totalBytes;
            String pattern = "##.###";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(percent) + "%";
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
        private final int id;
        private final long time;
        private final long startTime;
        private final long promotedBytes;
        private final long retainedBytes;
        private final long clearedBytes;
        private final String threads;
        private final boolean full;

        public GCData(int id, long time, long startTime,
                      long promotedBytes, long retainedBytes,
                      long clearedBytes, String threads, boolean full) {
            this.id = id;
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
