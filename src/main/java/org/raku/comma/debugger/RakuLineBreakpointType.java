package org.raku.comma.debugger;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.filetypes.RakuScriptFileType;
import org.raku.comma.filetypes.RakuTestFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuLineBreakpointType extends XLineBreakpointType<RakuLineBreakpointProperties> {
    public RakuLineBreakpointType() {
        super("rakuLineBreakpoint", "Raku Line Breakpoint");
    }

    @Nullable
    @Override
    public RakuLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return null;
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return file.getFileType() instanceof RakuScriptFileType
            || file.getFileType() instanceof RakuModuleFileType
            || file.getFileType() instanceof RakuTestFileType;
    }

    @Nullable
    @Override
    public XDebuggerEditorsProvider getEditorsProvider(@NotNull XLineBreakpoint<RakuLineBreakpointProperties> breakpoint,
                                                       @NotNull Project project) {
        return RakuDebuggerEditorsProvider.INSTANCE;
    }
}
