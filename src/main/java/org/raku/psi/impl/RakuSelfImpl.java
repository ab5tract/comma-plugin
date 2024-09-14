package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.RakuSelf;
import org.raku.psi.RakuSelfReference;
import org.raku.psi.type.RakuSelfType;
import org.raku.psi.type.RakuType;
import org.raku.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;

public class RakuSelfImpl extends ASTWrapperPsiElement implements RakuSelf {
    public RakuSelfImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RakuSelfReference(this);
    }

    @Override
    public @NotNull RakuType inferType() {
        RakuPackageDecl packageDecl = PsiTreeUtil.getParentOfType(this, RakuPackageDecl.class);
        return packageDecl != null
                ? new RakuSelfType(packageDecl)
                : RakuUntyped.INSTANCE;
    }
}
