package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuContextualizer;
import org.jetbrains.annotations.NotNull;

public class RakuContextualizerImpl extends ASTWrapperPsiElement implements RakuContextualizer {
    public RakuContextualizerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
