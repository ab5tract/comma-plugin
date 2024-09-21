package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuietly;
import org.jetbrains.annotations.NotNull;

public class RakuQuietlyImpl extends RakuASTWrapperPsiElement implements RakuQuietly {
    public RakuQuietlyImpl(@NotNull ASTNode node) {
        super(node);
    }
}
