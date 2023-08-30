package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6SemiList;

public abstract class Perl6ContextualizerSurrounder<T extends PsiElement> extends Perl6Surrounder<T> {
    public Perl6ContextualizerSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected PsiElement insertStatements(T surrounder, PsiElement[] statements) {
        Perl6SemiList semiList = PsiTreeUtil.getChildOfType(surrounder, Perl6SemiList.class);
        if (semiList != null) {
            semiList.add(Perl6ElementFactory.createNewLine(semiList.getProject()));
            for (PsiElement statement : statements) semiList.add(statement.copy());
            semiList.add(Perl6ElementFactory.createNewLine(semiList.getProject()));
        }
        return null;
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return null;
    }

    @Override
    protected boolean isExpression() {
        return true;
    }

    @Override
    protected boolean isControl() {
        return false;
    }
}
