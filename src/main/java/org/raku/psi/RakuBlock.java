package org.raku.psi;

public interface RakuBlock extends RakuPsiElement, RakuPsiScope, RakuExtractable {
    RakuBlockoid getBlockoid();
}
