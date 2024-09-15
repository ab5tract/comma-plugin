package org.raku.comma.cro.template.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface CroTemplateCall extends PsiNamedElement {
    PsiElement[] getCallArguments();
}