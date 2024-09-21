package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuBlockoid;
import org.jetbrains.annotations.NotNull;

public class RakuBlockoidImpl extends RakuASTWrapperPsiElement implements RakuBlockoid {
    public RakuBlockoidImpl(@NotNull ASTNode node) {
        super(node);
    }
}
