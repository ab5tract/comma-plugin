package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHashIndex;
import org.jetbrains.annotations.NotNull;

public class RakuHashIndexImpl extends ASTWrapperPsiElement implements RakuHashIndex {
    public RakuHashIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
