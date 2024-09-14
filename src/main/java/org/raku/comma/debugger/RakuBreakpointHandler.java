package org.raku.comma.debugger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class RakuBreakpointHandler extends XBreakpointHandler {
    private final RakuDebugThread debugThread;
    private static final Logger LOG = Logger.getInstance(RakuBreakpointHandler.class);

    RakuBreakpointHandler(RakuDebugThread debugThread) {
        super(RakuLineBreakpointType.class);
        this.debugThread = debugThread;
    }

    @Override
    public void registerBreakpoint(@NotNull XBreakpoint breakpoint) {
        if (breakpoint instanceof XLineBreakpoint) {
            debugThread.queueBreakpoint((XLineBreakpoint<?>)breakpoint, false);
        } else {
            LOG.warn("Unknown breakpoint during register action");
        }
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint breakpoint, boolean temporary) {
        if (breakpoint instanceof XLineBreakpoint) {
            debugThread.queueBreakpoint((XLineBreakpoint<?>)breakpoint, true);
        } else {
            LOG.warn("Unknown breakpoint during un-register action");
        }
    }
}
