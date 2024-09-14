package org.raku.editor;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.raku.psi.PodPreComment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuEnterDelegate implements EnterHandlerDelegate {
    private boolean shouldAppendComment = false;

    @Override
    public Result preprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  @Nullable EditorActionHandler originalHandler) {
        PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
        if (caretOffset.get() == 0) return null;
        PsiElement element = file.findElementAt(caretOffset.get() - 1);
        if (element == null) return null;
        shouldAppendComment = element.getParent() instanceof PodPreComment;
        return null;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
        if (shouldAppendComment) {
            int offset = editor.getCaretModel().getOffset();
            editor.getDocument().insertString(offset, "#| ");
            editor.getCaretModel().moveToOffset(offset + 3); // Move caret along with insertion
        }

        shouldAppendComment = false;
        return null;
    }
}
