package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import edument.perl6idea.debugger.DebugUtils;
import org.jetbrains.annotations.NotNull;

public abstract class Perl6DebugEventBreakpointBase extends Perl6DebugEventBase implements Perl6DebugEventBreakpoint {
    protected String path;
    protected int line;

    @Override
    public void run() {
        XLineBreakpoint bp = DebugUtils.findBreakpoint(getDebugSession().getProject(), this);
        if (bp != null) {
            processBreakPoint(bp, getDebugSession());
        }
    }

    protected abstract void processBreakPoint(@NotNull XLineBreakpoint bp, XDebugSession session);

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getLine() {
        return line;
    }
}
