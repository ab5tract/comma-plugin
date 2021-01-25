package edument.perl6idea.psi;

import org.jetbrains.annotations.NotNull;

public interface Perl6LongName extends Perl6PsiElement {
    String getNameWithoutColonPairs();
    @NotNull Perl6ColonPair[] getColonPairs();
}
