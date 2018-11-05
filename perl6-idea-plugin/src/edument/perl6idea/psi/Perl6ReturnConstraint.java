package edument.perl6idea.psi;

import org.jetbrains.annotations.Nullable;

public interface Perl6ReturnConstraint extends Perl6PsiElement {
    @Nullable
    String getReturnType();
}
