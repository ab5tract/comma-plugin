package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuPhaser;
import org.jetbrains.annotations.NotNull;

public class RakuPhaserImpl extends RakuASTWrapperPsiElement implements RakuPhaser {
    public RakuPhaserImpl(@NotNull ASTNode node) {
        super(node);
    }
}
