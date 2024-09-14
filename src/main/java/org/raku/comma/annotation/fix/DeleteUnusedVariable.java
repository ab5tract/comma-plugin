package org.raku.comma.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.RakuStatement;
import org.raku.comma.psi.RakuVariable;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

public class DeleteUnusedVariable implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Safe delete unused variable";
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
        PsiElement caretEl = file.findElementAt(editor.getCaretModel().getOffset());
        if (caretEl == null)
            return;
        RakuVariable variable = PsiTreeUtil.getNonStrictParentOfType(caretEl, RakuVariable.class);
        RakuVariableDecl decl = PsiTreeUtil.getParentOfType(caretEl, RakuVariableDecl.class);
        assert decl != null;
        RakuPsiUtil.deleteElementDocComments(PsiTreeUtil.getParentOfType(decl, RakuStatement.class));
        decl.removeVariable(variable);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
