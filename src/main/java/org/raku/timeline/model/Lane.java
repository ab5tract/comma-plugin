package org.raku.timeline.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A lane is an individual row in the timeline. It exists as part of a lane group,
 * which ensures we don't ever render two tasks on top of each other. */
public class Lane {
    private final List<Logged> entries = new ArrayList<>();
    private final Map<String, LaneGroup> childTaskLaneGroups = new LinkedHashMap<>();

    public boolean tryAdd(Logged entry) {
        if (entry instanceof Event) {
            if (eventConflicts((Event)entry))
                return false;
        }
        else if (entry instanceof Task) {
            if (taskConflicts((Task)entry))
                return false;
        }
        entries.add(entry);
        entry.setLane(this);
        return true;
    }

    private boolean eventConflicts(Event entry) {
        for (Logged check : entries) {
            if (check instanceof Event) {
                if (((Event)check).getWhen() == entry.getWhen())
                    return true;
            }
            else if (check instanceof Task checkTask) {
                if (entry.getWhen() >= checkTask.getStart() && entry.getWhen() < checkTask.getEnd())
                    return true;
            }
        }
        return false;
    }

    private boolean taskConflicts(Task entry) {
        for (Logged check : entries) {
            if (check instanceof Event) {
                double when = ((Event)check).getWhen();
                if (when >= entry.getStart() && when < entry.getEnd())
                    return true;
            }
            else if (check instanceof Task checkTask) {
                double intersectionLower = Math.max(entry.getStart(), checkTask.getStart());
                double intersectionUpper = Math.min(entry.getEnd(), checkTask.getEnd());
                if (intersectionLower <= intersectionUpper)
                    return true;
            }
        }
        return false;
    }

    public List<Logged> getEntries() {
        return entries;
    }

    public void addToChildLane(Logged child) {
        LaneGroup group = childTaskLaneGroups.computeIfAbsent(child.getName(), n -> new LaneGroup());
        group.add(child);
    }

    public Map<String, LaneGroup> getChildTaskLaneGroups() {
        return childTaskLaneGroups;
    }
}
