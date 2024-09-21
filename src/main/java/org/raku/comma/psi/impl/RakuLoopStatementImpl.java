package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuLoopStatement;
import org.jetbrains.annotations.NotNull;

public class RakuLoopStatementImpl extends RakuASTWrapperPsiElement implements RakuLoopStatement {
    public RakuLoopStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
