package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStatementModCond;
import org.jetbrains.annotations.NotNull;

public class RakuStatementModCondImpl extends RakuASTWrapperPsiElement implements RakuStatementModCond {
    public RakuStatementModCondImpl(@NotNull ASTNode node) {
        super(node);
    }
}
