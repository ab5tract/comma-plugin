package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuUnlessStatement;
import org.jetbrains.annotations.NotNull;

public class RakuUnlessStatementImpl extends ASTWrapperPsiElement implements RakuUnlessStatement {
    public RakuUnlessStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
