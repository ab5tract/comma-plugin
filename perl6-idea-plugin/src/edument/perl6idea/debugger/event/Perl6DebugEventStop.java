package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XSuspendContext;
import edument.perl6idea.debugger.Perl6DebugThread;
import edument.perl6idea.debugger.Perl6StackFrameDescriptor;
import edument.perl6idea.debugger.Perl6SuspendContext;
import edument.perl6idea.debugger.Perl6ThreadDescriptor;

public class Perl6DebugEventStop extends Perl6DebugEventBase implements Perl6DebugEvent {
    private final Perl6ThreadDescriptor[] threads;
    private final int activeThreadIndex;

    public Perl6DebugEventStop(Perl6ThreadDescriptor[] threads, int activeThreadIndex, XDebugSession session, Perl6DebugThread thread) {
        setDebugSession(session);
        setDebugThread(thread);
        this.threads = threads;
        this.activeThreadIndex = activeThreadIndex;
    }

    XSuspendContext getSuspendContext() {
        return new Perl6SuspendContext(threads, activeThreadIndex, getDebugSession(), getDebugThread());
    }

    @Override
    public void run() {
        getDebugSession().positionReached(getSuspendContext());
    }
}
