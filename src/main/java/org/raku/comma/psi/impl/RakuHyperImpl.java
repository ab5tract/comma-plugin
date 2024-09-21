package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHyper;
import org.jetbrains.annotations.NotNull;

public class RakuHyperImpl extends RakuASTWrapperPsiElement implements RakuHyper {
    public RakuHyperImpl(@NotNull ASTNode node) {
        super(node);
    }
}
