package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRequireStatement;
import org.jetbrains.annotations.NotNull;

public class RakuRequireStatementImpl extends ASTWrapperPsiElement implements RakuRequireStatement {
    public RakuRequireStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
