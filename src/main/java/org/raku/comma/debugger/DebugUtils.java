package org.raku.comma.debugger;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.raku.comma.debugger.event.RakuDebugEventBreakpoint;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;

public class DebugUtils {
    @Nullable
    public static XLineBreakpoint<?> findBreakpoint(final Project project, final RakuDebugEventBreakpoint bp) {
        final XLineBreakpoint<?>[] result = new XLineBreakpoint[]{null};

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile file = VfsUtil.findFileByIoFile(new File(bp.getPath()), true);
            if (file == null)
                return;
            String virtualFileUrl = file.getUrl();

            Collection<? extends XLineBreakpoint<RakuLineBreakpointProperties>> breakpoints =
                    XDebuggerManager.getInstance(project).getBreakpointManager().getBreakpoints(RakuLineBreakpointType.class);
            for (XLineBreakpoint<RakuLineBreakpointProperties> breakpoint : breakpoints) {
                if (StringUtil.equals(breakpoint.getFileUrl(), virtualFileUrl) && breakpoint.getLine() == bp.getLine()) {
                    result[0] = breakpoint;
                    return;
                }
            }
        });
        return result[0];
    }
}
