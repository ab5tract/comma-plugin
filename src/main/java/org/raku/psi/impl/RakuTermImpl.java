package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuTerm;
import org.jetbrains.annotations.NotNull;

public class RakuTermImpl extends ASTWrapperPsiElement implements RakuTerm {
    public RakuTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
