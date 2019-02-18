package edument.perl6idea.profiler;

import java.util.List;

public class Perl6ProfileNavigationModel extends Perl6ProfileModel {
    public Perl6ProfileNavigationModel(List<Perl6ProfilerNode> calls) {
        super(calls);
        calculatePercentage();
    }

    private void calculatePercentage() {
        // Calculate inclusive time
        if (nodes.size() > 0) {
            inclusiveSum = nodes.get(0).getInclusiveTime();
        }
    }
}
