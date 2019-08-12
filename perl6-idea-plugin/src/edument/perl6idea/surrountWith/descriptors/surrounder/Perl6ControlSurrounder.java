package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.P6Control;

public abstract class Perl6ControlSurrounder<T extends P6Control> extends Perl6Surrounder<T> {
    public Perl6ControlSurrounder(boolean isStatement) {
        super(isStatement);
    }

    @Override
    protected void insertStatements(T surrounder, PsiElement[] statements) {
        surrounder.addStatements(statements);
    }

    @Override
    protected PsiElement getAnchor(T surrounder) {
        return surrounder.getTopic();
    }
}
