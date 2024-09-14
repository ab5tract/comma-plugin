package org.raku.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import org.raku.debugger.RakuDebugThread;

public interface RakuDebugEvent extends Runnable {
    @Override
    void run();

    XDebugSession getDebugSession();

    void setDebugSession(XDebugSession debugSession);

    RakuDebugThread getDebugThread();

    void setDebugThread(RakuDebugThread thread);
}
