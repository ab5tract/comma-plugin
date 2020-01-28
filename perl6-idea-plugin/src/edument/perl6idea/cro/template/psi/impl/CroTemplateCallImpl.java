package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateArgList;
import edument.perl6idea.cro.template.psi.CroTemplateCall;
import edument.perl6idea.cro.template.psi.reference.CroTemplateCallReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes.*;
import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

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

    @Override
    public PsiElement[] getCallArguments() {
        CroTemplateArgList list = PsiTreeUtil.getChildOfType(this, CroTemplateArgList.class);
        if (list == null) return PsiElement.EMPTY_ARRAY;
        PsiElement node = list.getFirstChild();

        List<PsiElement> args = new ArrayList<>();

        while (node != null) {
            if (!(node instanceof PsiWhiteSpace ||
                node.getNode().getElementType() == SYNTAX_WHITE_SPACE ||
                node.getNode().getElementType() == OPEN_PAREN ||
                node.getNode().getElementType() == CLOSE_PAREN ||
                node.getNode().getElementType() == COMMA)) {
                args.add(node);
            }
            node = node.getNextSibling();
        }

        return args.toArray(PsiElement.EMPTY_ARRAY);
    }
}
