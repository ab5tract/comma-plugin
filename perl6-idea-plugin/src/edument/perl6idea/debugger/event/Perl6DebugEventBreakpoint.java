package edument.perl6idea.debugger.event;

public interface Perl6DebugEventBreakpoint extends Perl6DebugEvent {
    String getPath();
    int getLine();
}
