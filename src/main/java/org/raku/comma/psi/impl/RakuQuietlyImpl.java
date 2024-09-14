package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuietly;
import org.jetbrains.annotations.NotNull;

public class RakuQuietlyImpl extends ASTWrapperPsiElement implements RakuQuietly {
    public RakuQuietlyImpl(@NotNull ASTNode node) {
        super(node);
    }
}
