package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.cro.template.psi.CroTemplatePart;
import org.raku.comma.cro.template.psi.CroTemplateSignature;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.comma.cro.template.psi.stub.CroTemplatePartStub;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.cro.template.parsing.CroTemplateTokenTypes.PART_NAME;

public class CroTemplatePartImpl extends StubBasedPsiElementBase<CroTemplatePartStub> implements CroTemplatePart {
    public CroTemplatePartImpl(@NotNull ASTNode node) {
        super(node);
    }

    public CroTemplatePartImpl(final CroTemplatePartStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        CroTemplateSignature signature = PsiTreeUtil.getChildOfType(this, CroTemplateSignature.class);
        if (signature != null)
            signature.offerAllParametersTo(collector);
    }

    @Override
    public String getName() {
        CroTemplatePartStub stub = getStub();
        if (stub != null)
            return stub.getName();

        ASTNode[] subName = getNode().getChildren(TokenSet.create(PART_NAME));
        return subName.length == 0 ? null : subName[0].getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        // FIXME No renaming for now
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(CroTemplate:PART)";
    }
}