package org.raku.comma.debugger;

public abstract class RakuValueDescriptor {
    private final String name;

    RakuValueDescriptor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getKind();
    public abstract String getType();
    public abstract String getValue();
    public abstract boolean isExpandableNode();

    public String getPresentableDescription(RakuDebugThread thread) {
        return getValue();
    }
}
