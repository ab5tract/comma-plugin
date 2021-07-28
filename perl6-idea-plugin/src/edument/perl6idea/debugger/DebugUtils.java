package edument.perl6idea.debugger;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import edument.perl6idea.debugger.event.Perl6DebugEventBreakpoint;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

public class DebugUtils {
    @Nullable
    public static XLineBreakpoint<?> findBreakpoint(final Project project, final Perl6DebugEventBreakpoint bp) {
        final XLineBreakpoint<?>[] result = new XLineBreakpoint[]{null};

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile file = VfsUtil.findFileByIoFile(new File(bp.getPath()), true);
            if (file == null)
                return;
            String virtualFileUrl = file.getUrl();

            Collection<? extends XLineBreakpoint<Perl6LineBreakpointProperties>> breakpoints =
                    XDebuggerManager.getInstance(project).getBreakpointManager().getBreakpoints(Perl6LineBreakpointType.class);
            for (XLineBreakpoint<Perl6LineBreakpointProperties> breakpoint : breakpoints) {
                if (StringUtil.equals(breakpoint.getFileUrl(), virtualFileUrl) && breakpoint.getLine() == bp.getLine()) {
                    result[0] = breakpoint;
                    return;
                }
            }
        });
        return result[0];
    }
}
