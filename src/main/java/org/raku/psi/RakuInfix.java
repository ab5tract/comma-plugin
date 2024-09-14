package org.raku.psi;

import com.intellij.psi.PsiElement;

public interface RakuInfix extends RakuPsiElement {
    PsiElement getLeftSide();
    PsiElement getRightSide();
    PsiElement getOperator();
}
