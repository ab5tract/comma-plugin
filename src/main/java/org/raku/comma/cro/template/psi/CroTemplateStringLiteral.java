package org.raku.comma.cro.template.psi;

import com.intellij.psi.PsiElement;

public interface CroTemplateStringLiteral extends PsiElement {
    String getStringValue();
}
