package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XSuspendContext;
import edument.perl6idea.debugger.Perl6DebugThread;
import edument.perl6idea.debugger.Perl6StackFrameDescriptor;
import edument.perl6idea.debugger.Perl6SuspendContext;

public class Perl6DebugEventStop extends Perl6DebugEventBase implements Perl6DebugEvent {
    private final Perl6StackFrameDescriptor[] frames;

    public Perl6DebugEventStop(Perl6StackFrameDescriptor[] frames, XDebugSession session, Perl6DebugThread thread) {
        setDebugSession(session);
        setDebugThread(thread);
        this.frames = frames;
    }

    XSuspendContext getSuspendContext() {
        return new Perl6SuspendContext(frames, getDebugSession(), getDebugThread());
    }

    @Override
    public void run() {
        getDebugSession().positionReached(getSuspendContext());
    }
}
