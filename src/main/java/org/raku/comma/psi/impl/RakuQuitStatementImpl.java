package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuitStatement;
import org.jetbrains.annotations.NotNull;

public class RakuQuitStatementImpl extends RakuASTWrapperPsiElement implements RakuQuitStatement {
    public RakuQuitStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
