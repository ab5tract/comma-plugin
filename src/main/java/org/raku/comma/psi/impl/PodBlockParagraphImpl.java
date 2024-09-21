package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockParagraph;
import org.jetbrains.annotations.NotNull;

public class PodBlockParagraphImpl extends RakuASTWrapperPsiElement implements PodBlockParagraph {
    public PodBlockParagraphImpl(@NotNull ASTNode node) {
        super(node);
    }

}
