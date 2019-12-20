package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateCall;
import edument.perl6idea.cro.template.psi.reference.CroTemplateCallReference;
import org.jetbrains.annotations.NotNull;

public class CroTemplateCallImpl extends ASTWrapperPsiElement implements CroTemplateCall {
    public CroTemplateCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new CroTemplateCallReference(this);
    }

    @Override
    public String getName() {
        ASTNode[] subName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.SUB_NAME));
        return subName.length == 0 ? null : subName[0].getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }
}
