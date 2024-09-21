package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockDelimited;
import org.jetbrains.annotations.NotNull;

public class PodBlockDelimitedImpl extends RakuASTWrapperPsiElement implements PodBlockDelimited {
    public PodBlockDelimitedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
