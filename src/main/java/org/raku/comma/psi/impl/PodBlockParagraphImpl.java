package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockParagraph;
import org.jetbrains.annotations.NotNull;

public class PodBlockParagraphImpl extends ASTWrapperPsiElement implements PodBlockParagraph {
    public PodBlockParagraphImpl(@NotNull ASTNode node) {
        super(node);
    }

}
