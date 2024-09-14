package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhenStatement;
import org.jetbrains.annotations.NotNull;

public class RakuWhenStatementImpl extends ASTWrapperPsiElement implements RakuWhenStatement {
    public RakuWhenStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
