package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuNegationMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuNegationMetaOpImpl extends ASTWrapperPsiElement implements RakuNegationMetaOp {
    public RakuNegationMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
