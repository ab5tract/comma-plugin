package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockFinish;
import org.jetbrains.annotations.NotNull;

public class PodBlockFinishImpl extends RakuASTWrapperPsiElement implements PodBlockFinish {
    public PodBlockFinishImpl(@NotNull ASTNode node) {
        super(node);
    }
}
