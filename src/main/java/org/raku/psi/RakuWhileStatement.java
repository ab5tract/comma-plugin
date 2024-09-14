package org.raku.psi;

public interface RakuWhileStatement extends RakuPsiElement, RakuExtractable {
    RakuBlock getBlock();
}
