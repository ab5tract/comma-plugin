package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRequireStatement;
import org.jetbrains.annotations.NotNull;

public class RakuRequireStatementImpl extends ASTWrapperPsiElement implements RakuRequireStatement {
    public RakuRequireStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
