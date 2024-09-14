package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuWhereConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuWhereConstraintImpl extends ASTWrapperPsiElement implements RakuWhereConstraint {
    public RakuWhereConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
