package edument.perl6idea.timeline.model;

import java.util.Map;

/* The common things that all logged items on the timeline have, be they
 * events or tasks. */
public abstract class Logged {
    private final String module;
    private final String category;
    private final String name;
    private final Map<String, Object> data;
    protected Lane lane;

    public Logged(String module, String category, String name, Map<String, Object> data) {
        this.module = module;
        this.category = category;
        this.name = name;
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }
}
