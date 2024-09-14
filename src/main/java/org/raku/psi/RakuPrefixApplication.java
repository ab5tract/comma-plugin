package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface RakuPrefixApplication extends RakuPsiElement, RakuExtractable {
    @Nullable PsiElement getOperand();
    @Nullable PsiElement getPrefix();
    boolean isAssignish();
}
