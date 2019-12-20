package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateSignature;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolKind;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSubImpl extends ASTWrapperPsiElement implements CroTemplateSub {
    public CroTemplateSubImpl(@NotNull ASTNode node) {
        super(node);
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
        ASTNode[] subName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.SUB_NAME));
        return subName.length == 0 ? null : subName[0].getText();
    }

    @Override
    public int getTextOffset() {
        ASTNode[] subName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.SUB_NAME));
        return subName.length == 0 ? super.getTextOffset() : subName[0].getStartOffset();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }
}
