package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import edument.perl6idea.debugger.Perl6DebugThread;

public abstract class Perl6DebugEventBase implements Perl6DebugEvent {
    private transient XDebugSession myDebugSession;
    private transient Perl6DebugThread myDebugThread;

    @Override
    public XDebugSession getDebugSession() {
        return myDebugSession;
    }

    @Override
    public void setDebugSession(XDebugSession debugSession) {
        myDebugSession = debugSession;
    }

    @Override
    public Perl6DebugThread getDebugThread() {
        return myDebugThread;
    }

    @Override
    public void setDebugThread(Perl6DebugThread thread) {
        myDebugThread = thread;
    }
}
