package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuImportStatement;
import org.jetbrains.annotations.NotNull;

public class RakuImportStatementImpl extends RakuASTWrapperPsiElement implements RakuImportStatement {
    public RakuImportStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
