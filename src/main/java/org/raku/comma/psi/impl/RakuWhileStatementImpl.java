package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuBlock;
import org.raku.comma.psi.RakuWhileStatement;
import org.jetbrains.annotations.NotNull;

public class RakuWhileStatementImpl extends RakuASTWrapperPsiElement implements RakuWhileStatement {
    public RakuWhileStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public RakuBlock getBlock() {
        return findChildByClass(RakuBlock.class);
    }
}
