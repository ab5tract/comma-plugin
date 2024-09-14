package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhereConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuWhereConstraintImpl extends ASTWrapperPsiElement implements RakuWhereConstraint {
    public RakuWhereConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
