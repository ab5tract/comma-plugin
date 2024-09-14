package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuIsTraitName;
import org.raku.psi.RakuIsTraitReference;
import org.raku.psi.RakuLongName;
import org.jetbrains.annotations.NotNull;

public class RakuIsTraitNameImpl extends ASTWrapperPsiElement implements RakuIsTraitName {
    public RakuIsTraitNameImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuIsTraitReference(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuLongName type = RakuElementFactory.createIsTraitName(getProject(), name);
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = type.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
