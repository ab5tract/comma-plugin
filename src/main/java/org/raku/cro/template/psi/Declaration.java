package org.raku.cro.template.psi;

import com.intellij.psi.PsiElement;
import org.raku.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface Declaration extends PsiElement {
    void declareToCollector(CroTemplateSymbolCollector collector);
}
