package org.raku.run;

public interface RakuDebuggableConfiguration {
    int getDebugPort();
    void setDebugPort(int debugPort);
    boolean isStartSuspended();
    void setStartSuspended(boolean startSuspended);
}
