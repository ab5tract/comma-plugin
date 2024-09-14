package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuTerm;
import org.jetbrains.annotations.NotNull;

public class RakuTermImpl extends ASTWrapperPsiElement implements RakuTerm {
    public RakuTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
