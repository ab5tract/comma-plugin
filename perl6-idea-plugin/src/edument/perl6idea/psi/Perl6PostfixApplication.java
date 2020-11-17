package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface Perl6PostfixApplication extends Perl6PsiElement, P6Extractable {
    @Nullable
    PsiElement getOperand();
    @Nullable
    PsiElement getPostfix();
    boolean isAssignish();
}
