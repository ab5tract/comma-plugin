package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.cro.template.psi.CroTemplateApply;
import org.raku.cro.template.psi.CroTemplateElementFactory;
import org.raku.cro.template.psi.CroTemplateMacro;
import org.raku.cro.template.psi.CroTemplateSignature;
import org.raku.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.cro.template.psi.reference.CroTemplateSymbolKind;
import org.raku.cro.template.psi.stub.CroTemplateMacroStub;
import org.jetbrains.annotations.NotNull;

import static org.raku.cro.template.parsing.CroTemplateTokenTypes.MACRO_NAME;

public class CroTemplateMacroImpl extends StubBasedPsiElementBase<CroTemplateMacroStub> implements CroTemplateMacro {
    public CroTemplateMacroImpl(@NotNull ASTNode node) {
        super(node);
    }

    public CroTemplateMacroImpl(final CroTemplateMacroStub stub, final IStubElementType nodeType) {
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
        collector.offer(getName(), CroTemplateSymbolKind.Macro, this);
    }

    @Override
    public String getName() {
        CroTemplateMacroStub stub = getStub();
        if (stub != null)
            return stub.getName();

        ASTNode[] macroName = getNode().getChildren(TokenSet.create(MACRO_NAME));
        return macroName.length == 0 ? null : macroName[0].getText();
    }

    @Override
    public int getTextOffset() {
        ASTNode[] macroName = getNode().getChildren(TokenSet.create(MACRO_NAME));
        return macroName.length == 0 ? super.getTextOffset() : macroName[0].getStartOffset();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        ASTNode oldNameNode = findChildByType(TokenSet.create(MACRO_NAME));
        if (oldNameNode != null) {
            CroTemplateApply newName = CroTemplateElementFactory.createMacroCall(getProject(), name);
            ASTNode newNameNode = newName.getNode().getChildren(TokenSet.create(MACRO_NAME))[0];
            CodeEditUtil.replaceChild(getNode(), oldNameNode, newNameNode);
        }
        return this;
    }
}
