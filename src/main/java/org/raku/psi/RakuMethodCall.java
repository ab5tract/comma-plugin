package org.raku.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface RakuMethodCall extends RakuPsiElement, RakuCodeBlockCall {
    @NotNull
    String getCallOperator();
    PsiElement getCallOperatorNode();
    PsiElement getSimpleName();
    boolean isTopicCall();
}
