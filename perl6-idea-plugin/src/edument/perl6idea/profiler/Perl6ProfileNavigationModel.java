package edument.perl6idea.profiler;

import java.util.List;

public class Perl6ProfileNavigationModel extends Perl6ProfileModel {
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

    @Override
    protected String calculateExclusiveValue(int time, int inclusiveTime) {
        String percents = DECIMAL_FORMAT.format(((double)time / inclusiveTime) * 100);
        return String.format("%s%% (%s μs)", percents, time);
    }
}
