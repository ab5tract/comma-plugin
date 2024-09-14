package org.raku.comma.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import org.raku.comma.debugger.RakuDebugThread;

public interface RakuDebugEvent extends Runnable {
    @Override
    void run();

    XDebugSession getDebugSession();

    void setDebugSession(XDebugSession debugSession);

    RakuDebugThread getDebugThread();

    void setDebugThread(RakuDebugThread thread);
}
