package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHashIndex;
import org.jetbrains.annotations.NotNull;

public class RakuHashIndexImpl extends RakuASTWrapperPsiElement implements RakuHashIndex {
    public RakuHashIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
