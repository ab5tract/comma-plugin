package org.raku.debugger.event;

public interface RakuDebugEventBreakpoint extends RakuDebugEvent {
    String getPath();
    int getLine();
}
