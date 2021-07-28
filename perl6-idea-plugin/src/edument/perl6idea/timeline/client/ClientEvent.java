package edument.perl6idea.timeline.client;

import java.util.Map;

public class ClientEvent {
    private final String module;
    private final String category;
    private final String name;
    private final int kind;
    private final double timestamp;
    private final int id;
    private final int parentId;
    private final Map<String, Object> data;

    public ClientEvent(String module, String category, String name,
                       int kind, double timestamp, int id, int parentId,
                       Map<String, Object> data) {
        this.module = module;
        this.category = category;
        this.name = name;
        this.kind = kind;
        this.timestamp = timestamp;
        this.id = id;
        this.parentId = parentId;
        this.data = data;
    }

    public String getModule() {
        return module;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public boolean isEvent() {
        return kind == 0;
    }

    public boolean isTaskStart() {
        return kind == 1;
    }

    public boolean isTaskEnd() {
        return kind == 2;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
