package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReduceMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuReduceMetaOpImpl extends RakuASTWrapperPsiElement implements RakuReduceMetaOp {
    public RakuReduceMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
