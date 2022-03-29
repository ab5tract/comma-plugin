package edument.perl6idea.psi;

public interface Perl6RegexPsiElement extends Perl6PsiElement {
    default boolean mightMatchZeroWidth() {
        return false;
    }

    default boolean atomsMightMatchZeroWidth(Perl6RegexAtom[] atoms) {
        // Everything in the sequence of atoms must potentially match nothing.
        if (atoms != null) {
            for (Perl6RegexAtom atom : atoms) {
                if (!atom.mightMatchZeroWidth())
                    return false;
            }
        }
        return true;
    }
}
