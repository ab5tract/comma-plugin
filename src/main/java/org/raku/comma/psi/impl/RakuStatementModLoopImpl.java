package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStatementModLoop;
import org.jetbrains.annotations.NotNull;

public class RakuStatementModLoopImpl extends RakuASTWrapperPsiElement implements RakuStatementModLoop {
    public RakuStatementModLoopImpl(@NotNull ASTNode node) {
        super(node);
    }
}
