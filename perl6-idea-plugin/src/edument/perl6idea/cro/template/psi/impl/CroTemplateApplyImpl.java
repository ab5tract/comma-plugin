package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateApply;
import edument.perl6idea.cro.template.psi.CroTemplateCall;
import edument.perl6idea.cro.template.psi.CroTemplateElementFactory;
import edument.perl6idea.cro.template.psi.reference.CroTemplateApplyReference;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes.MACRO_NAME;

public class CroTemplateApplyImpl extends ASTWrapperPsiElement implements CroTemplateApply {
    public CroTemplateApplyImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new CroTemplateApplyReference(this);
    }

    @Override
    public String getName() {
        ASTNode[] macroName = getNode().getChildren(TokenSet.create(MACRO_NAME));
        return macroName.length == 0 ? null : macroName[0].getText();
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
