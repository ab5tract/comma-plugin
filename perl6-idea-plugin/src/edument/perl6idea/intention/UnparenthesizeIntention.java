package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6ParenthesizedExpr;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.NotNull;

public class UnparenthesizeIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        Perl6ParenthesizedExpr expr = PsiTreeUtil.getParentOfType(element, Perl6ParenthesizedExpr.class);
        assert expr != null;

        Perl6Statement[] elements = expr.getElements();

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
        Perl6ParenthesizedExpr parenthesizedExpr = PsiTreeUtil.getParentOfType(element, Perl6ParenthesizedExpr.class);
        if (parenthesizedExpr == null || parenthesizedExpr.getParent() instanceof Perl6ColonPair)
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
