package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuNegationMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuNegationMetaOpImpl extends ASTWrapperPsiElement implements RakuNegationMetaOp {
    public RakuNegationMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
