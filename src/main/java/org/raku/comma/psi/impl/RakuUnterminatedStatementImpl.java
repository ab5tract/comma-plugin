package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUnterminatedStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUnterminatedStatementImpl extends RakuASTWrapperPsiElement implements RakuUnterminatedStatement {
    public RakuUnterminatedStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
