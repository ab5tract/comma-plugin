package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.raku.comma.psi.RakuPostfix;
import org.jetbrains.annotations.NotNull;

public class RakuPostfixImpl extends RakuASTWrapperPsiElement implements RakuPostfix {
    public RakuPostfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuOpReference(this);
    }
}
