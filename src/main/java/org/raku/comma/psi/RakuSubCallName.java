package org.raku.comma.psi;

import org.jetbrains.annotations.NotNull;

public interface RakuSubCallName extends RakuPsiElement {
    @NotNull
    String getCallName();

    boolean resolvesAsLexicalOperator();

    boolean resolvesLexically();
}
