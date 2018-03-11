package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6Variable extends Perl6PsiElement {
    PsiElement getVariableToken();
}
