package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;

public interface Perl6MethodCall extends Perl6PsiElement, P6CodeBlockCall {
    String getCallOperator();
    PsiElement getCallOperatorNode();
}
