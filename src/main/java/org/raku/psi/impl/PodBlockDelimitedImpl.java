package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.PodBlockDelimited;
import org.jetbrains.annotations.NotNull;

public class PodBlockDelimitedImpl extends ASTWrapperPsiElement implements PodBlockDelimited {
    public PodBlockDelimitedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
