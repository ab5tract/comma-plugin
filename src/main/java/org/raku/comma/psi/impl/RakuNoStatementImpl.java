package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuNoStatement;
import org.jetbrains.annotations.NotNull;

public class RakuNoStatementImpl extends RakuASTWrapperPsiElement implements RakuNoStatement {
    public RakuNoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
