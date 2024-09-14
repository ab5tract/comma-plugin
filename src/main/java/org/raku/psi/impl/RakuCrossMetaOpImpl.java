package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuCrossMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuCrossMetaOpImpl extends ASTWrapperPsiElement implements RakuCrossMetaOp {
    public RakuCrossMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
