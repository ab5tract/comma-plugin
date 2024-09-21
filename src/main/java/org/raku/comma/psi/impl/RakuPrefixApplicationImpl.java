package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuPrefix;
import org.raku.comma.psi.RakuPrefixApplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuPrefixApplicationImpl extends RakuASTWrapperPsiElement implements RakuPrefixApplication {
    public RakuPrefixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getOperand() {
        return getLastChild();
    }

    @Nullable
    @Override
    public PsiElement getPrefix() {
        return getFirstChild();
    }

    @Override
    public boolean isAssignish() {
        PsiElement prefix = getPrefix();
        if (prefix instanceof RakuPrefix) {
            String operator = prefix.getText();
            return operator.equals("++") || operator.equals("--") ||
                   operator.equals("⚛++") || operator.equals("⚛--");
        }
        return false;
    }
}
