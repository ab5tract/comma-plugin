package org.raku.cro.template.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import org.raku.cro.template.psi.CroTemplateFile;
import org.raku.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface CroTemplateFileStub extends PsiFileStub<CroTemplateFile> {
    void declareExportedSymbols(CroTemplateSymbolCollector collector);
}
