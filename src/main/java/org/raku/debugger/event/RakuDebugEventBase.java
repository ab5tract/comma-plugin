package org.raku.debugger.event;

import com.intellij.xdebugger.XDebugSession;
import org.raku.debugger.RakuDebugThread;

public abstract class RakuDebugEventBase implements RakuDebugEvent {
    private transient XDebugSession myDebugSession;
    private transient RakuDebugThread myDebugThread;

    @Override
    public XDebugSession getDebugSession() {
        return myDebugSession;
    }

    @Override
    public void setDebugSession(XDebugSession debugSession) {
        myDebugSession = debugSession;
    }

    @Override
    public RakuDebugThread getDebugThread() {
        return myDebugThread;
    }

    @Override
    public void setDebugThread(RakuDebugThread thread) {
        myDebugThread = thread;
    }
}
