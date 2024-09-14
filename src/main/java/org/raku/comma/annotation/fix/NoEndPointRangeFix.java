package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class NoEndPointRangeFix implements IntentionAction {
    private final int offset;

    public NoEndPointRangeFix(int offset) {
        this.offset = offset;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add * for infinite range";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        editor.getDocument().insertString(offset, "*");
        editor.getCaretModel().moveToOffset(offset);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
