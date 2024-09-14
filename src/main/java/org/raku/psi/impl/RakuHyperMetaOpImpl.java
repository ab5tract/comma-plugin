package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuHyperMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuHyperMetaOpImpl extends ASTWrapperPsiElement implements RakuHyperMetaOp {
    public RakuHyperMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
