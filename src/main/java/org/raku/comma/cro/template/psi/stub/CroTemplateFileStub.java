package org.raku.comma.cro.template.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import org.raku.comma.cro.template.psi.CroTemplateFile;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface CroTemplateFileStub extends PsiFileStub<CroTemplateFile> {
    void declareExportedSymbols(CroTemplateSymbolCollector collector);
}
