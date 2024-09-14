package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuNullTerm;
import org.jetbrains.annotations.NotNull;

public class RakuNullTermImpl extends ASTWrapperPsiElement implements RakuNullTerm {
    public RakuNullTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
