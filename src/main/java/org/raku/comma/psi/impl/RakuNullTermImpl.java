package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuNullTerm;
import org.jetbrains.annotations.NotNull;

public class RakuNullTermImpl extends RakuASTWrapperPsiElement implements RakuNullTerm {
    public RakuNullTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
