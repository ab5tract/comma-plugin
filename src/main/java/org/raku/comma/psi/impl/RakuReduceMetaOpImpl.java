package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReduceMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuReduceMetaOpImpl extends ASTWrapperPsiElement implements RakuReduceMetaOp {
    public RakuReduceMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
