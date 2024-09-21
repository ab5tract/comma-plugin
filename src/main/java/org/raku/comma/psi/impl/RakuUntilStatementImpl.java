package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUntilStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUntilStatementImpl extends RakuASTWrapperPsiElement implements RakuUntilStatement {
    public RakuUntilStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
