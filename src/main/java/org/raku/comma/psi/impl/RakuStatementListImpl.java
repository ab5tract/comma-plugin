package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStatementList;
import org.jetbrains.annotations.NotNull;

public class RakuStatementListImpl extends RakuASTWrapperPsiElement implements RakuStatementList {
    public RakuStatementListImpl(@NotNull ASTNode node) {
        super(node);
    }
}
