package edument.perl6idea.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class Perl6SuspendContext extends XSuspendContext {
    private final XDebugSession myDebugSession;
    private final Perl6DebugThread myDebugThread;
    private final Perl6ExecutionStack myExecutionStack;

    public Perl6SuspendContext(Perl6StackFrameDescriptor[] frames, XDebugSession debugSession, Perl6DebugThread debugThread) {
        myDebugThread = debugThread;
        myDebugSession = debugSession;
        myExecutionStack = new Perl6ExecutionStack(frames, this);
    }

    @Nullable
    @Override
    public XExecutionStack getActiveExecutionStack() {
        return myExecutionStack;
    }

    @Override
    public void computeExecutionStacks(XExecutionStackContainer container) {
        container.addExecutionStack(Collections.singletonList(myExecutionStack), true);
    }

    public XDebugSession getDebugSession() {
        return myDebugSession;
    }

    @NotNull
    public Perl6DebugThread getDebugThread() {
        return myDebugThread;
    }
}