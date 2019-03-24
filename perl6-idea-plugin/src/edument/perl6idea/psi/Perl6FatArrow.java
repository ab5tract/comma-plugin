package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6FatArrow extends Perl6PsiElement {
    String getKey();
    PsiElement getValue();
}
