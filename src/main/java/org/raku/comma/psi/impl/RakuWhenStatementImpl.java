package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhenStatement;
import org.jetbrains.annotations.NotNull;

public class RakuWhenStatementImpl extends RakuASTWrapperPsiElement implements RakuWhenStatement {
    public RakuWhenStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
