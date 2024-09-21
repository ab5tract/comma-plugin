package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuSemiList;
import org.jetbrains.annotations.NotNull;

public class RakuSemiListImpl extends RakuASTWrapperPsiElement implements RakuSemiList {
    public RakuSemiListImpl(@NotNull ASTNode node) {
        super(node);
    }
}
