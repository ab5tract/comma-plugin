package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuValueConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuValueConstraintImpl extends ASTWrapperPsiElement implements RakuValueConstraint {
    public RakuValueConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
