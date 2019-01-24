package edument.perl6idea.debugger;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Perl6ExecutionStack extends XExecutionStack {
    private final Perl6SuspendContext mySuspendContext;
    private List<Perl6StackFrame> stackFrames = new ArrayList<>();

    public Perl6ExecutionStack(String threadName, Perl6StackFrameDescriptor[] frames, Perl6SuspendContext suspendContext) {
        super(threadName);
        mySuspendContext = suspendContext;
        for (Perl6StackFrameDescriptor frame : frames) {
            stackFrames.add(new Perl6StackFrame(frame, this));
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

    public Perl6SuspendContext getSuspendContext() {
        return mySuspendContext;
    }
}
