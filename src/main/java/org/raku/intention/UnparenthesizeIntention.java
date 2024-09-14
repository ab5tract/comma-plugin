package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuColonPair;
import org.raku.psi.RakuParenthesizedExpr;
import org.raku.psi.RakuStatement;
import org.jetbrains.annotations.NotNull;

public class UnparenthesizeIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RakuParenthesizedExpr expr = PsiTreeUtil.getParentOfType(element, RakuParenthesizedExpr.class);
        assert expr != null;

        RakuStatement[] elements = expr.getElements();

        if (elements.length == 1) {
            String statement = elements[0].getText();
            editor.getDocument().replaceString(
                expr.getTextOffset(),
                expr.getTextOffset() + expr.getTextLength(),
                statement);
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        RakuParenthesizedExpr parenthesizedExpr = PsiTreeUtil.getParentOfType(element, RakuParenthesizedExpr.class);
        if (parenthesizedExpr == null || parenthesizedExpr.getParent() instanceof RakuColonPair)
            return false;
        return parenthesizedExpr.getElements().length == 1;
    }

    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Remove parentheses";
    }
}
