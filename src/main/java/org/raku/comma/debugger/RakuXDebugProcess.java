package org.raku.comma.debugger;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class RakuXDebugProcess extends XDebugProcess {
    private final ExecutionResult myExecutionResult;
    private final RakuDebugThread myDebugThread;

    RakuXDebugProcess(XDebugSession session, ExecutionResult result) {
        super(session);
        myExecutionResult = result;
        myDebugThread = new RakuDebugThread(session, result);
        myDebugThread.start();
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return RakuDebuggerEditorsProvider.INSTANCE;
    }

    @Override
    public boolean checkCanInitBreakpoints() {
        return true;
    }

    @Override
    public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
        return new XBreakpointHandler[]{new RakuBreakpointHandler(myDebugThread)};
    }

    @Override
    public void startPausing() {
        myDebugThread.pauseExecution();
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        myDebugThread.stepOver(getActiveThreadId(context));
    }

    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        myDebugThread.stepInto(getActiveThreadId(context));
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        myDebugThread.stepOut(getActiveThreadId(context));
    }

    private static int getActiveThreadId(@Nullable XSuspendContext context) {
        if (context == null)
            return 1;
        XExecutionStack stack = context.getActiveExecutionStack();
        int threadId = 1;
        if (stack instanceof RakuExecutionStack)
            threadId = ((RakuExecutionStack)stack).getThreadId();
        return threadId;
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        myDebugThread.resumeExecution();
    }

    @Override
    public void stop() {
        myDebugThread.stopDebugThread();
    }

    @Nullable
    @Override
    protected ProcessHandler doGetProcessHandler() {
        return myExecutionResult.getProcessHandler();
    }

    @NotNull
    @Override
    public ExecutionConsole createConsole() {
        return myExecutionResult.getExecutionConsole();
    }
}
