package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuStatementModLoop;
import org.jetbrains.annotations.NotNull;

public class RakuStatementModLoopImpl extends ASTWrapperPsiElement implements RakuStatementModLoop {
    public RakuStatementModLoopImpl(@NotNull ASTNode node) {
        super(node);
    }
}
