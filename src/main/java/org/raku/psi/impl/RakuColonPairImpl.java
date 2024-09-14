package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.*;
import org.raku.psi.type.RakuType;
import org.raku.sdk.RakuSdkType;
import org.raku.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

import static org.raku.parsing.RakuTokenTypes.COLON_PAIR;

public class RakuColonPairImpl extends ASTWrapperPsiElement implements RakuColonPair {
    public RakuColonPairImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Pair);
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
