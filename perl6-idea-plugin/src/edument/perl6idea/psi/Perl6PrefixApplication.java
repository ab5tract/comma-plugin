package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface Perl6PrefixApplication extends Perl6PsiElement, P6Extractable {
    @Nullable PsiElement getOperand();
    @Nullable PsiElement getPrefix();
    boolean isAssignish();
}
