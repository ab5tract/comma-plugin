package edument.perl6idea.psi;

public interface Perl6RegexPsiElement extends Perl6PsiElement {
    default boolean mightMatchZeroWidth() {
        return false;
    }
}
