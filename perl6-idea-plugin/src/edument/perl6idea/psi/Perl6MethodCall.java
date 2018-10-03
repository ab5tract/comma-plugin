package edument.perl6idea.psi;

import com.intellij.psi.PsiNamedElement;

public interface Perl6MethodCall extends Perl6PsiElement, PsiNamedElement {
    String getCallName();
    String getCallOperator();
}
