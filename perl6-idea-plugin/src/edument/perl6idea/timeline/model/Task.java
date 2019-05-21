package edument.perl6idea.timeline.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Task extends Logged {
    private double start;
    private double end;
    private List<Logged> children;

    public Task(String module, String category, String name, Map<String, Object> data, double start) {
        super(module, category, name, data);
        this.start = start;
        this.end = Double.POSITIVE_INFINITY;
        this.children = new ArrayList<>();
    }

    public boolean hasEnded() {
        return end != Double.POSITIVE_INFINITY;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public void endTask(double when) {
        this.end = when;
    }

    public void addChild(Logged child) {
        this.children.add(child);
    }

    public List<Logged> getChildren() {
        return this.children;
    }
}
