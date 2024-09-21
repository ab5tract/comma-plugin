package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuOnce;
import org.jetbrains.annotations.NotNull;

public class RakuOnceImpl extends RakuASTWrapperPsiElement implements RakuOnce {
    public RakuOnceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
