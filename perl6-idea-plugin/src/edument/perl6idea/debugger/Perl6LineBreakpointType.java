package edument.perl6idea.debugger;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.filetypes.Perl6TestFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class Perl6LineBreakpointType extends XLineBreakpointType<Perl6LineBreakpointProperties> {
    public Perl6LineBreakpointType() {
        super("perl6LineBreakpoint", "Perl6 Line Breakpoint");
    }

    @Nullable
    @Override
    public Perl6LineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return null;
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return file.getFileType() instanceof Perl6ScriptFileType
            || file.getFileType() instanceof Perl6ModuleFileType
            || file.getFileType() instanceof Perl6TestFileType;
    }

    @Nullable
    @Override
    public XDebuggerEditorsProvider getEditorsProvider(@NotNull XLineBreakpoint<Perl6LineBreakpointProperties> breakpoint,
                                                       @NotNull Project project) {
        return Perl6DebuggerEditorsProvider.INSTANCE;
    }
}
