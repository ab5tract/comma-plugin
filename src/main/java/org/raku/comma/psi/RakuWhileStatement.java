package org.raku.comma.psi;

public interface RakuWhileStatement extends RakuPsiElement, RakuExtractable {
    RakuBlock getBlock();
}
