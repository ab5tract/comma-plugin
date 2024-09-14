package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.raku.cro.template.parsing.CroTemplateTokenTypes;
import org.raku.cro.template.psi.CroTemplateElementFactory;
import org.raku.cro.template.psi.CroTemplateParameter;
import org.jetbrains.annotations.NotNull;

public class CroTemplateParameterImpl extends ASTWrapperPsiElement implements CroTemplateParameter {
    public CroTemplateParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        ASTNode[] varName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        return varName.length == 0 ? null : varName[0].getText();
    }

    @Override
    public int getTextOffset() {
        ASTNode[] varName = getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        return varName.length == 0 ? 0 : varName[0].getStartOffset();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return replace(CroTemplateElementFactory.createSubParameter(getProject(), name));
    }
}
