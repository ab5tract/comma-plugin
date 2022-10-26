package edument.perl6idea.surrountWith.descriptors.surrounder;

import com.intellij.psi.PsiElement;

public abstract class Perl6StringQuotesSurrounder<T extends PsiElement> extends Perl6Surrounder<T> {
    public Perl6StringQuotesSurrounder() {
        super(false);
    }
}
