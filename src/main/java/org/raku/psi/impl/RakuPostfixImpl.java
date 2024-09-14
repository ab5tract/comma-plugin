package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.raku.psi.RakuPostfix;
import org.jetbrains.annotations.NotNull;

public class RakuPostfixImpl extends ASTWrapperPsiElement implements RakuPostfix {
    public RakuPostfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuOpReference(this);
    }
}
