package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuBracketedInfix;
import org.jetbrains.annotations.NotNull;

public class RakuBracketedInfixImpl extends RakuASTWrapperPsiElement implements RakuBracketedInfix {
    public RakuBracketedInfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuOpReference(this);
    }
}
