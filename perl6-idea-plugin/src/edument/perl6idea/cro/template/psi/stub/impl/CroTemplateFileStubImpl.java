package edument.perl6idea.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolKind;
import edument.perl6idea.cro.template.psi.stub.CroTemplateFileStub;
import edument.perl6idea.cro.template.psi.stub.CroTemplateMacroStub;
import edument.perl6idea.cro.template.psi.stub.CroTemplateSubStub;

public class CroTemplateFileStubImpl extends PsiFileStubImpl<CroTemplateFile> implements CroTemplateFileStub {
    public CroTemplateFileStubImpl(CroTemplateFile file) {
        super(file);
    }

    @Override
    public void declareExportedSymbols(CroTemplateSymbolCollector collector) {
        for (StubElement child : getChildrenStubs()) {
            if (child instanceof CroTemplateSubStub)
                collector.offer(((CroTemplateSubStub)child).getName(), CroTemplateSymbolKind.Sub,
                        child.getPsi());
            else if (child instanceof CroTemplateMacroStub)
                collector.offer(((CroTemplateMacroStub)child).getName(), CroTemplateSymbolKind.Macro,
                        child.getPsi());
        }
    }
}
