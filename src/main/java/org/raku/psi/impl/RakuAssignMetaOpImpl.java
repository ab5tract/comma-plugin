package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuAssignMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuAssignMetaOpImpl extends ASTWrapperPsiElement implements RakuAssignMetaOp {
    public RakuAssignMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
