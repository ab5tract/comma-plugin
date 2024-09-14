package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuLoopStatement;
import org.jetbrains.annotations.NotNull;

public class RakuLoopStatementImpl extends ASTWrapperPsiElement implements RakuLoopStatement {
    public RakuLoopStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
