package edument.perl6idea.debugger;

public class Perl6ThreadDescriptor {
    private int threadId;
    private long nativeThreadId;
    private Perl6StackFrameDescriptor[] stackFrames;

    public Perl6ThreadDescriptor(int threadId, long nativeThreadId, Perl6StackFrameDescriptor[] stackFrames) {
        this.threadId = threadId;
        this.nativeThreadId = nativeThreadId;
        this.stackFrames = stackFrames;
    }

    public int getThreadId() {
        return threadId;
    }

    public long getNativeThreadId() {
        return nativeThreadId;
    }

    public Perl6StackFrameDescriptor[] getStackFrames() {
        return stackFrames;
    }

    public String getDescription() {
        return "Thread " + Integer.toString(threadId) + " (native " + Long.toString(nativeThreadId) + ")";
    }
}
