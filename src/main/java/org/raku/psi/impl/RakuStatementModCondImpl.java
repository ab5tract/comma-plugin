package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuStatementModCond;
import org.jetbrains.annotations.NotNull;

public class RakuStatementModCondImpl extends ASTWrapperPsiElement implements RakuStatementModCond {
    public RakuStatementModCondImpl(@NotNull ASTNode node) {
        super(node);
    }
}
