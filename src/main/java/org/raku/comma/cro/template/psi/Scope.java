package org.raku.comma.cro.template.psi;

import com.intellij.psi.PsiElement;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface Scope extends PsiElement {
    void offerAllTo(CroTemplateSymbolCollector collector);
}
