package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUnterminatedStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUnterminatedStatementImpl extends ASTWrapperPsiElement implements RakuUnterminatedStatement {
    public RakuUnterminatedStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
