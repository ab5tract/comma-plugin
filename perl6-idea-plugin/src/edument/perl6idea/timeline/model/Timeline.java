package edument.perl6idea.timeline.model;

import edument.perl6idea.timeline.client.ClientEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Timeline {
    private Map<Integer, Task> activeTasks = new HashMap<>();
    private Map<String, Map<String, Map<String, List<Logged>>>> topLevel = new LinkedHashMap<>();
    private double firstTimestamp = 0.0;
    private Instant ourIinstantAtFirstTimestap;
    private double endTime = 0.0;

    public void incorporate(ClientEvent clientEvent) {
        // We deal in times relative to the timestamp of the first event we see.
        if (firstTimestamp == 0) {
            firstTimestamp = clientEvent.getTimestamp();
            ourIinstantAtFirstTimestap = Instant.now();
        }
        double timestamp = clientEvent.getTimestamp() - firstTimestamp;
        if (timestamp > endTime)
            endTime = timestamp;

        // Add the event.
        if (clientEvent.isEvent()) {
            Event event = new Event(clientEvent.getModule(), clientEvent.getCategory(), clientEvent.getName(),
                                    clientEvent.getData(), timestamp);
            add(event, clientEvent.getParentId());
        }
        else if (clientEvent.isTaskStart()) {
            Task task = new Task(clientEvent.getModule(), clientEvent.getCategory(), clientEvent.getName(),
                                 clientEvent.getData(), timestamp);
            activeTasks.put(clientEvent.getId(), task);
            add(task, clientEvent.getParentId());
        }
        else if (clientEvent.isTaskEnd()) {
            Task found = activeTasks.remove(clientEvent.getId());
            if (found != null)
                found.endTask(timestamp);
        }
    }

    private void add(Logged logged, int parent) {
        Task parentTask = activeTasks.get(parent);
        if (parentTask != null) {
            parentTask.addChild(logged);
            if (!logged.getModule().equals(parentTask.getModule()) ||
                    !logged.getCategory().equals(parentTask.getCategory()))
                addTopLevel(logged);
        }
        else {
            addTopLevel(logged);
        }
    }

    private void addTopLevel(Logged logged) {
        Map<String, Map<String, List<Logged>>> module = topLevel
                .computeIfAbsent(logged.getModule(), m -> new LinkedHashMap<>());
        Map<String, List<Logged>> category = module
                .computeIfAbsent(logged.getCategory(), c -> new LinkedHashMap<>());
        List<Logged> name = category
                .computeIfAbsent(logged.getName(), n -> new ArrayList<>());
        name.add(logged);
    }

    public void tick() {
        if (ourIinstantAtFirstTimestap != null) {
            double elapsed = (double)Duration.between(ourIinstantAtFirstTimestap, Instant.now()).toMillis() / 1000;
            if (elapsed > endTime)
                endTime = elapsed;
        }
    }

    public boolean isEmpty() {
        return topLevel.isEmpty();
    }

    public Map<String, Map<String, Map<String, List<Logged>>>> getData() {
        return topLevel;
    }

    public double getEndTime() {
        return endTime;
    }
}
