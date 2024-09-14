package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.raku.psi.RakuBracketedInfix;
import org.jetbrains.annotations.NotNull;

public class RakuBracketedInfixImpl extends ASTWrapperPsiElement implements RakuBracketedInfix {
    public RakuBracketedInfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuOpReference(this);
    }
}
