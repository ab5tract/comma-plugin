package edument.perl6idea.timeline;

/** Event produced by the timeline chart when the number of lanes in
 * view or the total number of lanes changes. */
public class VisibleLanesChanged {
    private final int totalLanes;
    private final int firstLaneInView;
    private final int lanesInView;

    public VisibleLanesChanged(int totalLanes, int firstLaneInView, int lanesInView) {
        this.totalLanes = totalLanes;
        this.firstLaneInView = firstLaneInView;
        this.lanesInView = lanesInView;
    }

    public int getTotalLanes() {
        return totalLanes;
    }

    public int getFirstLaneInView() {
        return firstLaneInView;
    }

    public int getLanesInView() {
        return lanesInView;
    }
}
