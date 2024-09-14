package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.raku.psi.RakuPrefix;
import org.jetbrains.annotations.NotNull;

public class RakuPrefixImpl extends ASTWrapperPsiElement implements RakuPrefix {
    public RakuPrefixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuOpReference(this);
    }
}
