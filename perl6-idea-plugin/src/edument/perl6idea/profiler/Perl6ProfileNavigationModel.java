package edument.perl6idea.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6ProfileNavigationModel extends Perl6ProfileModel {
    protected ArrayList<String> COLUMN_NAMES = new ArrayList<>(
        Arrays.asList("Name", "File", "Inclusive (μs)", "Exclusive (μs)", "Entries")
    );

    public Perl6ProfileNavigationModel(List<Perl6ProfilerNode> calls) {
        super(calls);
    }

    @Override
    protected void calculatePercentage() {
        // Calculate inclusive time
        if (nodes.size() > 0) {
            inclusiveSum = nodes.get(0).getInclusiveTime();
        }
    }

    protected String calculateExclusiveValue(int time) {
        String percents = DECIMAL_FORMAT.format(((double)time / inclusiveSum) * 100);
        return String.format("%s%% (%s μs)", percents, time);
    }

    @Override
    public int getColumnCount() {
        return 5;
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
                return profilerNode.getInclusiveTime();
            case 3:
                return profilerNode.getExclusiveTime();
            default:
                return profilerNode.getCallCount();
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES.get(column);
    }

    @Override
    public boolean needsSpecialRendering(int column) {
        return column == 2 || column == 3;
    }

    @Override
    public String renderNode(int column, Object value) {
        if (column == 2) {
            return calculateInclusiveValue((Integer)value);
        } else if (column == 3) {
            return calculateExclusiveValue((Integer)value);
        }
        return "";
    }
}
