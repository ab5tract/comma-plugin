package org.raku.comma.debugger;

public class RakuXAttributeValue extends RakuXNamedValue {
    private final RakuDebugThread debugThread;

    public RakuXAttributeValue(RakuValueDescriptor descriptor, RakuDebugThread debugThread) {
        super(descriptor);
        this.debugThread = debugThread;
    }

    @Override
    protected RakuDebugThread getDebugThread() {
        return debugThread;
    }
}
