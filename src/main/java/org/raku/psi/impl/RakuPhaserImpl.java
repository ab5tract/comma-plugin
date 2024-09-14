package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuPhaser;
import org.jetbrains.annotations.NotNull;

public class RakuPhaserImpl extends ASTWrapperPsiElement implements RakuPhaser {
    public RakuPhaserImpl(@NotNull ASTNode node) {
        super(node);
    }
}
