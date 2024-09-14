package org.raku.comma.debugger;

public class RakuXLexicalValue extends RakuXNamedValue {
    private final RakuStackFrame myStackFrame;

    public RakuXLexicalValue(RakuValueDescriptor descriptor, RakuStackFrame stackFrame) {
        super(descriptor);
        this.myStackFrame = stackFrame;
    }

    @Override
    protected RakuDebugThread getDebugThread() {
        return myStackFrame.getDebugThread();
    }
}
