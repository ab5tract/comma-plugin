package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuitStatement;
import org.jetbrains.annotations.NotNull;

public class RakuQuitStatementImpl extends ASTWrapperPsiElement implements RakuQuitStatement {
    public RakuQuitStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
