package org.raku.comma.debugger.event;

public interface RakuDebugEventBreakpoint extends RakuDebugEvent {
    String getPath();
    int getLine();
}
