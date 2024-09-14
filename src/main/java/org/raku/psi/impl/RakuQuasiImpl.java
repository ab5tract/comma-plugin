package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuQuasi;
import org.jetbrains.annotations.NotNull;

public class RakuQuasiImpl extends ASTWrapperPsiElement implements RakuQuasi {
    public RakuQuasiImpl(@NotNull ASTNode node) {
        super(node);
    }
}
