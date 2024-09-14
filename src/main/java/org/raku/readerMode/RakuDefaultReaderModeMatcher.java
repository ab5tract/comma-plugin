package org.raku.readerMode;

import com.intellij.codeInsight.actions.ReaderModeMatcher;
import com.intellij.codeInsight.actions.ReaderModeProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.raku.filetypes.RakuModuleFileType;
import org.raku.filetypes.RakuPodFileType;
import org.raku.filetypes.RakuScriptFileType;
import org.raku.filetypes.RakuTestFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuDefaultReaderModeMatcher implements ReaderModeMatcher {
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
        if (fileType == RakuScriptFileType.INSTANCE ||
            fileType == RakuModuleFileType.INSTANCE ||
            fileType == RakuTestFileType.INSTANCE ||
            fileType == RakuPodFileType.INSTANCE)
            return false;
        return null;
    }
}
