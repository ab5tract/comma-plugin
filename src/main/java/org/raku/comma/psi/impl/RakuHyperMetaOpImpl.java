package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHyperMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuHyperMetaOpImpl extends ASTWrapperPsiElement implements RakuHyperMetaOp {
    public RakuHyperMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
