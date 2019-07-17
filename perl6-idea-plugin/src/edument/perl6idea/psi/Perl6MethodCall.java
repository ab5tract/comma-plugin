package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface Perl6MethodCall extends Perl6PsiElement, P6CodeBlockCall {
    @NotNull
    String getCallOperator();
    PsiElement getCallOperatorNode();
}
