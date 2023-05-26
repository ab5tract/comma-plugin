package edument.perl6idea.debugger;

public record Perl6ThreadDescriptor(int threadId, long nativeThreadId, Perl6StackFrameDescriptor[] stackFrames) {

    public String getDescription() {
        return "Thread " + threadId + " (native " + nativeThreadId + ")";
    }
}
