package org.raku.comma.psi;

public interface RakuPointyBlock extends RakuPsiElement, RakuPsiScope, RakuExtractable, RakuControl {
    RakuParameter[] getParams();
    String getLambda();
}
