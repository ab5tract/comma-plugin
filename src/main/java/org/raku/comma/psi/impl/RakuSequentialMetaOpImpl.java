package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuSequentialMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuSequentialMetaOpImpl extends RakuASTWrapperPsiElement implements RakuSequentialMetaOp {
    public RakuSequentialMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
