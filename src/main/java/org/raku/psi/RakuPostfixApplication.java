package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface RakuPostfixApplication extends RakuPsiElement, RakuExtractable {
    @Nullable
    PsiElement getOperand();
    @Nullable
    PsiElement getPostfix();
    boolean isAssignish();
}
