package org.raku.debugger;

public class RakuNativeValueDescriptor extends RakuValueDescriptor {
    private final String kind;
    private final String value;

    RakuNativeValueDescriptor(String name, String kind, String value) {
        super(name);
        this.kind = kind;
        this.value = value;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String getType() {
        return kind;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isExpandableNode() {
        return false;
    }
}
