package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUntilStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUntilStatementImpl extends ASTWrapperPsiElement implements RakuUntilStatement {
    public RakuUntilStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
