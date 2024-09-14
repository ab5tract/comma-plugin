package org.raku.comma.psi;

public interface RakuRegexPsiElement extends RakuPsiElement {
    default boolean mightMatchZeroWidth() {
        return false;
    }

    default boolean atomsMightMatchZeroWidth(RakuRegexAtom[] atoms) {
        // Everything in the sequence of atoms must potentially match nothing.
        if (atoms != null) {
            for (RakuRegexAtom atom : atoms) {
                if (!atom.mightMatchZeroWidth())
                    return false;
            }
        }
        return true;
    }
}
