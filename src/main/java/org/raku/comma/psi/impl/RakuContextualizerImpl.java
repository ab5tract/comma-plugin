package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuContextualizer;
import org.jetbrains.annotations.NotNull;

public class RakuContextualizerImpl extends RakuASTWrapperPsiElement implements RakuContextualizer {
    public RakuContextualizerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
