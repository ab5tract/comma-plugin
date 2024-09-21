package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodText;
import org.jetbrains.annotations.NotNull;

public class PodTextImpl extends RakuASTWrapperPsiElement implements PodText {
    public PodTextImpl(@NotNull ASTNode node) {
        super(node);
    }
}
