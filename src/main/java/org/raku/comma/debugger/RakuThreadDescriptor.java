package org.raku.comma.debugger;

public record RakuThreadDescriptor(int threadId, long nativeThreadId, RakuStackFrameDescriptor[] stackFrames) {

    public String getDescription() {
        return "Thread " + threadId + " (native " + nativeThreadId + ")";
    }
}
