package org.raku.psi;

import org.jetbrains.annotations.NotNull;

public interface RakuSubCallName extends RakuPsiElement {
    @NotNull
    String getCallName();
}
