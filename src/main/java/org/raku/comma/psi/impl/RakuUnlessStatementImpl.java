package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUnlessStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUnlessStatementImpl extends RakuASTWrapperPsiElement implements RakuUnlessStatement {
    public RakuUnlessStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
