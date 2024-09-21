package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuNegationMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuNegationMetaOpImpl extends RakuASTWrapperPsiElement implements RakuNegationMetaOp {
    public RakuNegationMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
