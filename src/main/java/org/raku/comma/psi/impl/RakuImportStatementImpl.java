package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuImportStatement;
import org.jetbrains.annotations.NotNull;

public class RakuImportStatementImpl extends ASTWrapperPsiElement implements RakuImportStatement {
    public RakuImportStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
