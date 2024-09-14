package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.RakuStatement;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

public class RemoveUnusedRoutineFix implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Safe delete routine";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement el = file.findElementAt(editor.getCaretModel().getOffset());
        if (el == null)
            return;
        PsiElement decl = PsiTreeUtil.getParentOfType(el, RakuRoutineDecl.class);
        if (decl == null)
            return;
        RakuPsiUtil.deleteElementDocComments(PsiTreeUtil.getParentOfType(decl, RakuStatement.class));
        PsiElement maybeWS = decl.getParent().getNextSibling();
        if (maybeWS instanceof PsiWhiteSpace)
            maybeWS.delete();
        decl.getParent().delete();
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
