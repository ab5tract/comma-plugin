package org.raku.psi;

import org.raku.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;

public interface RakuReturnConstraint extends RakuPsiElement {
    @NotNull
    RakuType getReturnType();
}
