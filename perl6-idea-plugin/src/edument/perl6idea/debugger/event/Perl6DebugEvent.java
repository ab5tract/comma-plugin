package edument.perl6idea.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import edument.perl6idea.debugger.Perl6DebugThread;

public interface Perl6DebugEvent extends Runnable {
    @Override
    void run();

    XDebugSession getDebugSession();

    void setDebugSession(XDebugSession debugSession);

    Perl6DebugThread getDebugThread();

    void setDebugThread(Perl6DebugThread thread);
}
