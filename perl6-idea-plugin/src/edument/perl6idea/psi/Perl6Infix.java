package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6Infix extends Perl6PsiElement {
    PsiElement getLeftSide();
    PsiElement getRightSide();
    String getOperator();
}
