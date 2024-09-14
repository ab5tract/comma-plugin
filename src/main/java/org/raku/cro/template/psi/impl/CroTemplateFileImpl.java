package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.cro.template.CroTemplateFileType;
import org.raku.cro.template.CroTemplateLanguage;
import org.raku.cro.template.psi.CroTemplateFile;
import org.raku.cro.template.psi.Declaration;
import org.raku.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.cro.template.psi.stub.CroTemplateFileStub;
import org.jetbrains.annotations.NotNull;

public class CroTemplateFileImpl extends PsiFileBase implements CroTemplateFile {
    public CroTemplateFileImpl(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CroTemplateLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CroTemplateFileType.INSTANCE;
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        Declaration[] types = PsiTreeUtil.getChildrenOfType(this, Declaration.class);
        if (types == null)
            return;
        for (Declaration declaration : types) {
            declaration.declareToCollector(collector);
        }
    }

    @Override
    public void declareExportedSymbols(CroTemplateSymbolCollector collector) {
        StubElement<?> stub = getStub();
        if (stub instanceof CroTemplateFileStub)
            ((CroTemplateFileStub)stub).declareExportedSymbols(collector);
        else
            offerAllTo(collector);
    }
}
