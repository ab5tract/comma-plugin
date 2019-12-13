package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6InfixApplication extends Perl6PsiElement, P6Extractable {
    PsiElement[] getOperands();
    String getOperator();
}
