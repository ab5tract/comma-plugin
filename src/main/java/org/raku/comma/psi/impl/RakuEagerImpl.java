package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuEager;
import org.jetbrains.annotations.NotNull;

public class RakuEagerImpl extends RakuASTWrapperPsiElement implements RakuEager {
    public RakuEagerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
