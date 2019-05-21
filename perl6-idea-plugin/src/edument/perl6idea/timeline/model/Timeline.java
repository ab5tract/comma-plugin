package edument.perl6idea.timeline.model;

import edument.perl6idea.timeline.client.ClientEvent;

import java.util.*;

public class Timeline {
    private Map<Integer, Task> activeTasks;
    private Map<String, Map<String, Map<String, List<Logged>>>> topLevel;

    public Timeline() {
        activeTasks = new HashMap<>();
        topLevel = new LinkedHashMap<>();
    }

    public void incorporate(ClientEvent clientEvent) {
        if (clientEvent.isEvent()) {
            Event event = new Event(clientEvent.getModule(), clientEvent.getCategory(), clientEvent.getName(),
                                    clientEvent.getData(), clientEvent.getTimestamp());
            add(event, clientEvent.getParentId());
        }
        else if (clientEvent.isTaskStart()) {
            Task task = new Task(clientEvent.getModule(), clientEvent.getCategory(), clientEvent.getName(),
                                 clientEvent.getData(), clientEvent.getTimestamp());
            activeTasks.put(clientEvent.getId(), task);
            add(task, clientEvent.getParentId());
        }
        else if (clientEvent.isTaskEnd()) {
            Task found = activeTasks.remove(clientEvent.getId());
            if (found != null)
                found.endTask(clientEvent.getTimestamp());
        }
    }

    private void add(Logged logged, int parent) {
        Task parentTask = activeTasks.get(parent);
        if (parentTask != null)
            parentTask.addChild(logged);
        else
            addTopLevel(logged);
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
}
