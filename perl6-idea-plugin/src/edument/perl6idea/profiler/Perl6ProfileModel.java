package edument.perl6idea.profiler;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6ProfileModel extends AbstractTableModel {
    protected static final ArrayList<String> COLUMN_NAMES = new ArrayList<>(
        Arrays.asList("Name", "File", "Inclusive (μs)", "Entries")
    );
    protected int inclusiveSum;
    protected List<Perl6ProfilerNode> nodes;
    protected boolean showRealFileNames = false;
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    @Override
    public int getRowCount() {
        return nodes.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public Perl6ProfileModel(List<Perl6ProfilerNode> routines) {
        nodes = routines;
        calculatePercentage();
    }

    protected void calculatePercentage() {
        // Calculate inclusive time as sum of all inclusive times of calls in the table
        // It is correct for related call tables and is overridden in navigation table
        inclusiveSum = nodes.stream().mapToInt(p -> p.getInclusiveTime()).sum();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Perl6ProfilerNode profilerNode = nodes.get(row);
        switch (column) {
            case 0:
                return profilerNode.getName();
            case 1:
                return showRealFileNames ? profilerNode.getOriginalFile() : profilerNode.getFilename();
            case 2:
                return calculateInclusiveValue(profilerNode.getInclusiveTime());
            default:
                return profilerNode.getCallCount();
        }
    }

    protected Object calculateInclusiveValue(int timeInMills) {
        String percents = DECIMAL_FORMAT.format(((double)timeInMills / inclusiveSum) * 100);
        return String.format("%s%% (%s μs)", percents, timeInMills);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
            case 2:
                return String.class;
            default:
                return Integer.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES.get(column);
    }

    public boolean isCellInternal(int row, String path) {
        return !nodes.get(row).getOriginalFile().startsWith(path);
    }

    public int getNodeId(int row) {
        return nodes.get(row).getCallRoutineId();
    }

    public String getNodeSourceFile(int row) {
        return nodes.get(row).getOriginalFile();
    }

    public int getNodeSourceLine(int row) {
        return nodes.get(row).getLine();
    }

    public void setShowRealFileNames(boolean showRealFileNames) {
        this.showRealFileNames = showRealFileNames;
    }

    public boolean getShowRealFileNames() {
        return showRealFileNames;
    }

    public int getNavigationIndexByCallId(int id) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            Perl6ProfilerNode node = nodes.get(i);
            if (node.getCallRoutineId() == id)
                return i;
        }
        return -1;
    }
}
