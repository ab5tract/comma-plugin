package org.raku.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuSubCall;
import org.jetbrains.annotations.NotNull;

public class TermUnicodeConversionIntention extends TermConversionIntention {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Convert term to Unicode form";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        Operation operation = classifyOperation(project, editor, file);
        return operation == Operation.TO_UNICODE_PI || operation == Operation.TO_UNICODE_TAU ||
               operation == Operation.TO_UNICODE_SET;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        Operation operation = classifyOperation(project, editor, file);
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementUnderCaret = file.findElementAt(offset);
        assert elementUnderCaret != null;
        if (operation == Operation.TO_UNICODE_SET) {
            RakuSubCall call = PsiTreeUtil.getParentOfType(elementUnderCaret, RakuSubCall.class);
            assert call != null;
            TextRange elementRange = call.getTextRange();
            editor.getDocument().replaceString(elementRange.getStartOffset(), elementRange.getEndOffset(), "∅");
        }
        else if (operation == Operation.TO_UNICODE_PI) {
            TextRange elementRange = elementUnderCaret.getTextRange();
            editor.getDocument().replaceString(elementRange.getStartOffset(), elementRange.getEndOffset(), "π");
        }
        else if (operation == Operation.TO_UNICODE_TAU) {
            TextRange elementRange = elementUnderCaret.getTextRange();
            editor.getDocument().replaceString(elementRange.getStartOffset(), elementRange.getEndOffset(), "τ");
        }
    }
}
