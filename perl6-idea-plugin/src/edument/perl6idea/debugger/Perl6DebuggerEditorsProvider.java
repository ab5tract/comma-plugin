package edument.perl6idea.debugger;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6DebuggerEditorsProvider extends XDebuggerEditorsProviderBase {
    public static final Perl6DebuggerEditorsProvider INSTANCE = new Perl6DebuggerEditorsProvider();

    @Override
    protected PsiFile createExpressionCodeFragment(@NotNull Project project, @NotNull String text, @Nullable PsiElement context, boolean isPhysical) {
        return PsiFileFactory.getInstance(project).createFileFromText("file.dummy", getFileType(), text, 0, isPhysical);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Perl6ScriptFileType.INSTANCE;
    }
}
