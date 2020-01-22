package edument.perl6idea.cro.template.psi.stub;

import com.intellij.psi.stubs.PsiFileStub;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface CroTemplateFileStub extends PsiFileStub<CroTemplateFile> {
    void declareExportedSymbols(CroTemplateSymbolCollector collector);
}
