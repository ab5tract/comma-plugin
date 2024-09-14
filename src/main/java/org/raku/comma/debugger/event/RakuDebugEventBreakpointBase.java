package org.raku.comma.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.raku.comma.debugger.DebugUtils;
import org.jetbrains.annotations.NotNull;

public abstract class RakuDebugEventBreakpointBase extends RakuDebugEventBase implements RakuDebugEventBreakpoint {
    protected String path;
    protected int line;

    @Override
    public void run() {
        XLineBreakpoint<?> bp = DebugUtils.findBreakpoint(getDebugSession().getProject(), this);
        if (bp != null) {
            processBreakPoint(bp, getDebugSession());
        }
    }

    protected abstract void processBreakPoint(@NotNull XLineBreakpoint<?> bp, XDebugSession session);

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getLine() {
        return line;
    }
}
