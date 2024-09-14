package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.cro.template.psi.CroTemplateCall;
import org.raku.comma.cro.template.psi.CroTemplateElementFactory;
import org.raku.comma.cro.template.psi.CroTemplateSignature;
import org.raku.comma.cro.template.psi.CroTemplateSub;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.comma.cro.template.psi.reference.CroTemplateSymbolKind;
import org.raku.comma.cro.template.psi.stub.CroTemplateSubStub;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.cro.template.parsing.CroTemplateTokenTypes.SUB_NAME;

public class CroTemplateSubImpl extends StubBasedPsiElementBase<CroTemplateSubStub> implements CroTemplateSub {
    public CroTemplateSubImpl(@NotNull ASTNode node) {
        super(node);
    }

    public CroTemplateSubImpl(final CroTemplateSubStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getNode().getElementType() + ")";
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        CroTemplateSignature signature = PsiTreeUtil.getChildOfType(this, CroTemplateSignature.class);
        if (signature != null)
            signature.offerAllParametersTo(collector);
    }

    @Override
    public void declareToCollector(CroTemplateSymbolCollector collector) {
        collector.offer(getName(), CroTemplateSymbolKind.Sub, this);
    }

    @Override
    public String getName() {
        CroTemplateSubStub stub = getStub();
        if (stub != null)
            return stub.getName();

        ASTNode[] subName = getNode().getChildren(TokenSet.create(SUB_NAME));
        return subName.length == 0 ? null : subName[0].getText();
    }

    @Override
    public int getTextOffset() {
        ASTNode[] subName = getNode().getChildren(TokenSet.create(SUB_NAME));
        return subName.length == 0 ? super.getTextOffset() : subName[0].getStartOffset();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        ASTNode oldNameNode = findChildByType(TokenSet.create(SUB_NAME));
        if (oldNameNode != null) {
            CroTemplateCall newName = CroTemplateElementFactory.createSubCall(getProject(), name);
            ASTNode newNameNode = newName.getNode().getChildren(TokenSet.create(SUB_NAME))[0];
            CodeEditUtil.replaceChild(getNode(), oldNameNode, newNameNode);
        }
        return this;
    }
}
