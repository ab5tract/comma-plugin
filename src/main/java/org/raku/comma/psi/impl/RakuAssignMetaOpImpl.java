package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuAssignMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuAssignMetaOpImpl extends RakuASTWrapperPsiElement implements RakuAssignMetaOp {
    public RakuAssignMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
