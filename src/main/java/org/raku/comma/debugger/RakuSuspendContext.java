package org.raku.comma.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RakuSuspendContext extends XSuspendContext {
    private final XDebugSession myDebugSession;
    private final RakuDebugThread myDebugThread;
    private final List<RakuExecutionStack> myExecutionStacks;
    private final int activeThreadIndex;

    public RakuSuspendContext(RakuThreadDescriptor[] threads, int activeThreadIndex, XDebugSession debugSession, RakuDebugThread debugThread) {
        myDebugThread = debugThread;
        myDebugSession = debugSession;
        myExecutionStacks = new ArrayList<>();
        for (RakuThreadDescriptor thread : threads)
            myExecutionStacks.add(new RakuExecutionStack(thread, thread.stackFrames(), this));
        this.activeThreadIndex = activeThreadIndex;
    }

    @Nullable
    @Override
    public XExecutionStack getActiveExecutionStack() {
        return myExecutionStacks.get(activeThreadIndex);
    }

    @Override
    public void computeExecutionStacks(XExecutionStackContainer container) {
        container.addExecutionStack(myExecutionStacks, true);
    }

    public XDebugSession getDebugSession() {
        return myDebugSession;
    }

    @NotNull
    public RakuDebugThread getDebugThread() {
        return myDebugThread;
    }
}