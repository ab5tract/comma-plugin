package org.raku.comma.timeline.model;

import java.util.Map;

public class Event extends Logged {
    private final double when;

    public Event(String module, String category, String name, Map<String, Object> data, double when) {
        super(module, category, name, data);
        this.when = when;
    }

    public double getWhen() {
        return when;
    }
}
