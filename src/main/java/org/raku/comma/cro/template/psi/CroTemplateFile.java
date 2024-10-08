package org.raku.comma.cro.template.psi;

import com.intellij.psi.PsiFile;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface CroTemplateFile extends PsiFile, Scope {
    void declareExportedSymbols(CroTemplateSymbolCollector collector);
}
