package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRepeatStatement;
import org.jetbrains.annotations.NotNull;

public class RakuRepeatStatementImpl extends ASTWrapperPsiElement implements RakuRepeatStatement {
    public RakuRepeatStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
