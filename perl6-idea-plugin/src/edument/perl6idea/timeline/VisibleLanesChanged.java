package edument.perl6idea.timeline;

/**
 * Event produced by the timeline chart when the number of lanes in
 * view or the total number of lanes changes.
 */
public record VisibleLanesChanged(int totalLanes, int firstLaneInView, int lanesInView) {
}
