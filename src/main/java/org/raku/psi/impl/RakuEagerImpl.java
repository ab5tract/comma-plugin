package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuEager;
import org.jetbrains.annotations.NotNull;

public class RakuEagerImpl extends ASTWrapperPsiElement implements RakuEager {
    public RakuEagerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
