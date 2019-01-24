package edument.perl6idea.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perl6SuspendContext extends XSuspendContext {
    private final XDebugSession myDebugSession;
    private final Perl6DebugThread myDebugThread;
    private final List<Perl6ExecutionStack> myExecutionStacks;
    private final int activeThreadIndex;

    public Perl6SuspendContext(Perl6ThreadDescriptor[] threads, int activeThreadIndex, XDebugSession debugSession, Perl6DebugThread debugThread) {
        myDebugThread = debugThread;
        myDebugSession = debugSession;
        myExecutionStacks = new ArrayList<>();
        for (Perl6ThreadDescriptor thread : threads)
            myExecutionStacks.add(new Perl6ExecutionStack(thread, thread.getStackFrames(), this));
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
    public Perl6DebugThread getDebugThread() {
        return myDebugThread;
    }
}