package edument.perl6idea.psi;

import org.jetbrains.annotations.NotNull;

public interface Perl6SubCallName extends Perl6PsiElement {
    @NotNull
    String getCallName();
}
