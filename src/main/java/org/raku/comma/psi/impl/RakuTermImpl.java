package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuTerm;
import org.jetbrains.annotations.NotNull;

public class RakuTermImpl extends RakuASTWrapperPsiElement implements RakuTerm {
    public RakuTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
