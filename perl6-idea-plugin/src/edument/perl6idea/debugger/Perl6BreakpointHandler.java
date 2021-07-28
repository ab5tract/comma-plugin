package edument.perl6idea.debugger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class Perl6BreakpointHandler extends XBreakpointHandler {
    private final Perl6DebugThread debugThread;
    private static final Logger LOG = Logger.getInstance(Perl6BreakpointHandler.class);

    Perl6BreakpointHandler(Perl6DebugThread debugThread) {
        super(Perl6LineBreakpointType.class);
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
