package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.RakuElementFactory;
import org.raku.psi.RakuLongName;
import org.raku.psi.RakuModuleName;
import org.raku.psi.RakuModuleReference;
import org.jetbrains.annotations.NotNull;

public class RakuModuleNameImpl extends ASTWrapperPsiElement implements RakuModuleName {
    public RakuModuleNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuModuleReference(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuLongName moduleName = RakuElementFactory
            .createModuleName(getProject(), name);
        RakuLongName longName = findChildByClass(RakuLongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = moduleName.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
