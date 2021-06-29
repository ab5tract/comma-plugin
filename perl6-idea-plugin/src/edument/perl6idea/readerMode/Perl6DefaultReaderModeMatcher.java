package edument.perl6idea.readerMode;

import com.intellij.codeInsight.actions.ReaderModeMatcher;
import com.intellij.codeInsight.actions.ReaderModeProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.filetypes.Perl6PodFileType;
import edument.perl6idea.filetypes.Perl6ScriptFileType;
import edument.perl6idea.filetypes.Perl6TestFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6DefaultReaderModeMatcher implements ReaderModeMatcher {
    @Nullable
    @Override
    public Boolean matches(@NotNull Project project,
                           @NotNull VirtualFile file,
                           @Nullable Editor editor,
                           @NotNull ReaderModeProvider.ReaderMode mode) {
        if (editor == null)
            return false;
        @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null)
            return false;
        FileType fileType = psiFile.getFileType();
        if (fileType == Perl6ScriptFileType.INSTANCE ||
            fileType == Perl6ModuleFileType.INSTANCE ||
            fileType == Perl6TestFileType.INSTANCE ||
            fileType == Perl6PodFileType.INSTANCE)
            return false;
        return null;
    }
}
