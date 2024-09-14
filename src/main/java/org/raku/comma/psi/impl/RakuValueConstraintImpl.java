package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuValueConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuValueConstraintImpl extends ASTWrapperPsiElement implements RakuValueConstraint {
    public RakuValueConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
