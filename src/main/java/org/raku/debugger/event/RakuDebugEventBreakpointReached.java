package org.raku.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.raku.debugger.DebugUtils;
import org.raku.debugger.RakuDebugThread;
import org.raku.debugger.RakuThreadDescriptor;

public class RakuDebugEventBreakpointReached extends RakuDebugEventStop implements RakuDebugEventBreakpoint {
    private final String path;
    private final int line;

    public RakuDebugEventBreakpointReached(RakuThreadDescriptor[] threads, int activeThreadIndex, XDebugSession session, RakuDebugThread thread, String path, int line) {
        super(threads, activeThreadIndex, session, thread);
        this.path = path;
        this.line = line;
    }

    @Override
    public void run() {
        XDebugSession session = getDebugSession();
        XLineBreakpoint<?> breakpoint = DebugUtils.findBreakpoint(session.getProject(), this);
        if (breakpoint != null) {
            getDebugSession().breakpointReached(breakpoint, "", getSuspendContext());
        }
        super.run();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getLine() {
        return line;
    }
}
