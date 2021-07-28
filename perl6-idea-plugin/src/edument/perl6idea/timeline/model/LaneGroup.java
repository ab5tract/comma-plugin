package edument.perl6idea.timeline.model;

import java.util.ArrayList;
import java.util.List;

/** Some tasks may occur multiple times in parallel. Thus, we need multiple lanes to visualize
 * this in the graph. A lane group takes care of making sure we don't ever have overlap.
 */
public class LaneGroup {
    private final List<Lane> lanes = new ArrayList<>();

    public void add(Logged logged) {
        // Try to add to an existing lane.
        for (Lane lane : lanes)
            if (lane.tryAdd(logged))
                return;

        // If we get here, we need a new one; adding to an empty lane must work.
        Lane newLane = new Lane();
        newLane.tryAdd(logged);
        lanes.add(newLane);
    }

    public List<Lane> getLanes() {
        return lanes;
    }
}
