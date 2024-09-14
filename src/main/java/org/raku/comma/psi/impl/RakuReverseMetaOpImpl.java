package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReverseMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuReverseMetaOpImpl extends ASTWrapperPsiElement implements RakuReverseMetaOp {
    public RakuReverseMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
