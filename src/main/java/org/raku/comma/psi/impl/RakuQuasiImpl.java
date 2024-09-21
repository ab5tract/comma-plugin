package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuasi;
import org.jetbrains.annotations.NotNull;

public class RakuQuasiImpl extends RakuASTWrapperPsiElement implements RakuQuasi {
    public RakuQuasiImpl(@NotNull ASTNode node) {
        super(node);
    }
}
