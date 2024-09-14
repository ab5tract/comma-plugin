package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuHyper;
import org.jetbrains.annotations.NotNull;

public class RakuHyperImpl extends ASTWrapperPsiElement implements RakuHyper {
    public RakuHyperImpl(@NotNull ASTNode node) {
        super(node);
    }
}
