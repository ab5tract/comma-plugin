package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuContextualizer;
import org.jetbrains.annotations.NotNull;

public class RakuContextualizerImpl extends ASTWrapperPsiElement implements RakuContextualizer {
    public RakuContextualizerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
