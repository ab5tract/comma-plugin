package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRequireStatement;
import org.jetbrains.annotations.NotNull;

public class RakuRequireStatementImpl extends RakuASTWrapperPsiElement implements RakuRequireStatement {
    public RakuRequireStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
