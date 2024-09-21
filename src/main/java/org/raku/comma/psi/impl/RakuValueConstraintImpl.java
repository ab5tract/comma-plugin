package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuValueConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuValueConstraintImpl extends RakuASTWrapperPsiElement implements RakuValueConstraint {
    public RakuValueConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
