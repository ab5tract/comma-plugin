package org.raku.comma.psi;

import com.intellij.psi.PsiElement;

public interface RakuFatArrow extends RakuPsiElement {
    String getKey();
    PsiElement getValue();
}