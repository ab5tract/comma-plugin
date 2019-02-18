package edument.perl6idea.profiler;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class Perl6ProfileModel extends AbstractTableModel {
    protected List<Perl6ProfilerNode> nodes;

    @Override
    public int getRowCount() {
        return nodes.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    public Perl6ProfileModel(List<Perl6ProfilerNode> routines) {
        nodes = routines;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Perl6ProfilerNode profilerNode = nodes.get(row);
        switch (column) {
            case 0:
                return profilerNode.getName();
            case 1:
                return profilerNode.getFilename();
            case 2:
                return profilerNode.getInclusiveTime();
            case 3:
                return profilerNode.getExclusiveTime();
            default:
                return profilerNode.getCallCount();
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
                return String.class;
            default:
                return Integer.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "File";
            case 2:
                return "Inclusive time (μs)";
            case 3:
                return "Exclusive time (μs)";
            default:
                return "Call count";
        }
    }

    public boolean isCellInternal(int row, String path) {
        return !nodes.get(row).getOriginalFile().startsWith(path);
    }

    public int getNodeId(int row) {
        return nodes.get(row).getCallId();
    }

    public String getNodeSourceFile(int row) {
        return nodes.get(row).getOriginalFile();
    }

    public int getNodeSourceLine(int row) {
        return nodes.get(row).getLine();
    }

    public int getNavigationIndexByCallId(int id) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            Perl6ProfilerNode node = nodes.get(i);
            if (node.getCallId() == id)
                return i;
        }
        return -1;
    }
}
