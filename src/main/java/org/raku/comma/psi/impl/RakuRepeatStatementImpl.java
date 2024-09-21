package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRepeatStatement;
import org.jetbrains.annotations.NotNull;

public class RakuRepeatStatementImpl extends RakuASTWrapperPsiElement implements RakuRepeatStatement {
    public RakuRepeatStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
