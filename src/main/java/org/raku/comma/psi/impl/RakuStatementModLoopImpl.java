package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStatementModLoop;
import org.jetbrains.annotations.NotNull;

public class RakuStatementModLoopImpl extends ASTWrapperPsiElement implements RakuStatementModLoop {
    public RakuStatementModLoopImpl(@NotNull ASTNode node) {
        super(node);
    }
}
