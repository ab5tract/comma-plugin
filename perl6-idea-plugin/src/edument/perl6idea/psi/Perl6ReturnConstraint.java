package edument.perl6idea.psi;

import edument.perl6idea.psi.type.Perl6Type;
import org.jetbrains.annotations.NotNull;

public interface Perl6ReturnConstraint extends Perl6PsiElement {
    @NotNull
    Perl6Type getReturnType();
}
