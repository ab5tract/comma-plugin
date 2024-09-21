package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReverseMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuReverseMetaOpImpl extends RakuASTWrapperPsiElement implements RakuReverseMetaOp {
    public RakuReverseMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
