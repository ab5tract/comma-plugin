package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhereConstraint;
import org.jetbrains.annotations.NotNull;

public class RakuWhereConstraintImpl extends RakuASTWrapperPsiElement implements RakuWhereConstraint {
    public RakuWhereConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
