package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6ColonPair extends Perl6PsiElement {
    String getKey();
    PsiElement getStatement();
}
