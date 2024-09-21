package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWheneverStatement;
import org.jetbrains.annotations.NotNull;

public class RakuWheneverStatementImpl extends RakuASTWrapperPsiElement implements RakuWheneverStatement {
    public RakuWheneverStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
