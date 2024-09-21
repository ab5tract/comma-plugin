package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuPackageDecl;
import org.raku.comma.psi.RakuSelf;
import org.raku.comma.psi.RakuSelfReference;
import org.raku.comma.psi.type.RakuSelfType;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.jetbrains.annotations.NotNull;

public class RakuSelfImpl extends RakuASTWrapperPsiElement implements RakuSelf {
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
