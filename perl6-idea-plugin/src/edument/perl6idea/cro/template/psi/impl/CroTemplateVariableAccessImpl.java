package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateElementFactory;
import edument.perl6idea.cro.template.psi.CroTemplateVariableAccess;
import edument.perl6idea.cro.template.psi.reference.CroTemplateVariableReference;
import org.jetbrains.annotations.NotNull;

public class CroTemplateVariableAccessImpl extends ASTWrapperPsiElement implements CroTemplateVariableAccess {
    public CroTemplateVariableAccessImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        ASTNode[] varName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        return varName.length == 0 ? null : varName[0].getText();
    }

    @Override
    public PsiReference getReference() {
        return new CroTemplateVariableReference(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        CroTemplateVariableAccess access = CroTemplateElementFactory.createVariableAccess(getProject(), name);
        ASTNode[] newName = access.getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        ASTNode[] oldName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        if (newName.length == 1 && oldName.length == 1) {
            getNode().replaceChild(oldName[0], newName[0]);
        }
        return this;
    }
}
