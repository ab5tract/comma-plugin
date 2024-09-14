package org.raku.debugger;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RakuExecutionStack extends XExecutionStack {
    private final RakuSuspendContext mySuspendContext;
    private final List<RakuStackFrame> stackFrames = new ArrayList<>();
    private final RakuThreadDescriptor thread;

    public RakuExecutionStack(RakuThreadDescriptor thread, RakuStackFrameDescriptor[] frames, RakuSuspendContext suspendContext) {
        super(thread.getDescription());
        mySuspendContext = suspendContext;
        this.thread = thread;
        for (RakuStackFrameDescriptor frame : frames) {
            stackFrames.add(new RakuStackFrame(suspendContext.getDebugSession().getProject(), frame, this));
        }
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
        return ContainerUtil.getFirstItem(stackFrames);
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
        container.addStackFrames(stackFrames, true);
    }

    public RakuSuspendContext getSuspendContext() {
        return mySuspendContext;
    }

    public int getThreadId() {
        return thread.threadId();
    }
}
