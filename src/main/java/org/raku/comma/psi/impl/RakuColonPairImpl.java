package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.*;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

import static org.raku.comma.parsing.RakuTokenTypes.COLON_PAIR;

public class RakuColonPairImpl extends RakuASTWrapperPsiElement implements RakuColonPair {
    public RakuColonPairImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return lookupGlobalSymbol(RakuSettingTypeId.Pair);
    }

    @Override
    public String getKey() {
        PsiElement sibling = getFirstChild().getNextSibling();
        if (sibling.getNode().getElementType() == COLON_PAIR)
            return sibling.getText();
        else if (sibling instanceof RakuVariable) {
            String nameWithSigils = ((RakuVariable)sibling).getVariableName();
            if (nameWithSigils != null) {
                int sigilIndex = RakuVariable.getTwigil(nameWithSigils) == ' ' ? 1 : 2;
                return nameWithSigils.substring(sigilIndex);
            }
        } else {
            if (getLastChild().getNode().getElementType() == COLON_PAIR)
                return getLastChild().getText();
        }
        return null;
    }

    @Override
    public PsiElement getStatement() {
        PsiElement explicitStatement = PsiTreeUtil.findChildOfAnyType(this, RakuStatement.class, RakuStrLiteral.class, RakuVariable.class);
        if (explicitStatement == null) {
            if (getFirstChild().getText().equals(":")) {
                return RakuElementFactory.createStatementFromText(getProject(), "True;");
            } else {
                return RakuElementFactory.createStatementFromText(getProject(), "False;");
            }
        }
        return explicitStatement;
    }
}
