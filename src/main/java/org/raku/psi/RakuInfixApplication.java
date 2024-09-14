package org.raku.psi;

import com.intellij.psi.PsiElement;

public interface RakuInfixApplication extends RakuPsiElement, RakuExtractable {
    PsiElement[] getOperands();
    String getOperator();
    boolean isAssignish();
    default boolean isCommaOperator() {
        return ",".equals(getOperator());
    }
}
