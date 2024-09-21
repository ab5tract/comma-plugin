package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuCrossMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuCrossMetaOpImpl extends RakuASTWrapperPsiElement implements RakuCrossMetaOp {
    public RakuCrossMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
