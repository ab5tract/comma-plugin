package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6Try;
import edument.perl6idea.psi.Perl6WhenStatement;

public class Perl6TryCatchWhenSurrounder extends Perl6GenericTrySurrounder<Perl6Try> {
    public Perl6TryCatchWhenSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected String createBranch() {
        return "when True {}";
    }

    @Override
    protected PsiElement getAnchor(Perl6Try surrounder) {
        Perl6WhenStatement whenStatement = PsiTreeUtil.findChildOfType(surrounder.getBlock(), Perl6WhenStatement.class);
        return whenStatement == null ? null : whenStatement.getTopic();
    }

    @Override
    public String getTemplateDescription() {
        return "try { CATCH { when } }";
    }
}
