package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWheneverStatement;
import org.jetbrains.annotations.NotNull;

public class RakuWheneverStatementImpl extends ASTWrapperPsiElement implements RakuWheneverStatement {
    public RakuWheneverStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
