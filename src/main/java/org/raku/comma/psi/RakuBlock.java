package org.raku.comma.psi;

public interface RakuBlock extends RakuPsiElement, RakuPsiScope, RakuExtractable {
    RakuBlockoid getBlockoid();
}
