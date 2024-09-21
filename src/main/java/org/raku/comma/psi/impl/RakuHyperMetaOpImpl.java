package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHyperMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuHyperMetaOpImpl extends RakuASTWrapperPsiElement implements RakuHyperMetaOp {
    public RakuHyperMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
