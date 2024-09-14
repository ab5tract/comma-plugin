package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuStatementList;
import org.jetbrains.annotations.NotNull;

public class RakuStatementListImpl extends ASTWrapperPsiElement implements RakuStatementList {
    public RakuStatementListImpl(@NotNull ASTNode node) {
        super(node);
    }
}
