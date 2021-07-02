package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import edument.perl6idea.debugger.DebugUtils;
import edument.perl6idea.debugger.Perl6DebugThread;
import edument.perl6idea.debugger.Perl6ThreadDescriptor;

public class Perl6DebugEventBreakpointReached extends Perl6DebugEventStop implements Perl6DebugEventBreakpoint {
    private final String path;
    private final int line;

    public Perl6DebugEventBreakpointReached(Perl6ThreadDescriptor[] threads, int activeThreadIndex, XDebugSession session, Perl6DebugThread thread, String path, int line) {
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
