package org.raku.debugger;

public class RakuXAggregateValue extends RakuXNamedValue {
    private final RakuDebugThread debugThread;

    public RakuXAggregateValue(RakuValueDescriptor descriptor, RakuDebugThread debugThread) {
        super(descriptor);
        this.debugThread = debugThread;
    }

    @Override
    protected RakuDebugThread getDebugThread() {
        return debugThread;
    }
}
