package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuSequentialMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuSequentialMetaOpImpl extends ASTWrapperPsiElement implements RakuSequentialMetaOp {
    public RakuSequentialMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
