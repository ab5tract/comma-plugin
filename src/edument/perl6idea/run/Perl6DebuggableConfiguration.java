package edument.perl6idea.run;

public interface Perl6DebuggableConfiguration {
    int getDebugPort();
    void setDebugPort(int debugPort);
    boolean isStartSuspended();
    void setStartSuspended(boolean startSuspended);
}
