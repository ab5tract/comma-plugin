package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.PodBlockFinish;
import org.jetbrains.annotations.NotNull;

public class PodBlockFinishImpl extends ASTWrapperPsiElement implements PodBlockFinish {
    public PodBlockFinishImpl(@NotNull ASTNode node) {
        super(node);
    }
}
