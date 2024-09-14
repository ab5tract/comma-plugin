package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface RakuColonPair extends RakuPsiElement {
    @Nullable String getKey();
    PsiElement getStatement();
}
