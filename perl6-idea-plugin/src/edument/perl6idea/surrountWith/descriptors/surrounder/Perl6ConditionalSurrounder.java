package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.P6Conditional;
import edument.perl6idea.psi.Perl6ConditionalBranch;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.Perl6StatementList;

public abstract class Perl6ConditionalSurrounder<T extends P6Conditional> extends Perl6Surrounder<T> {
    public Perl6ConditionalSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected void insertStatements(T surrounder, PsiElement[] statements) {
        Perl6ConditionalBranch[] branches = surrounder.getBranches();
        for (Perl6ConditionalBranch branch : branches) {
            Perl6StatementList list = PsiTreeUtil.findChildOfType(branch.block, Perl6StatementList.class);
            if (list != null) {
                list.addRange(statements[0], statements[statements.length - 1]);
            }
        }
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return surrounder.getBranches()[0].condition;
    }
}
