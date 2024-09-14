package org.raku.comma.psi;

import org.jetbrains.annotations.NotNull;

public interface RakuLongName extends RakuPsiElement {
    String getNameWithoutColonPairs();
    @NotNull RakuColonPair[] getColonPairs();
}
