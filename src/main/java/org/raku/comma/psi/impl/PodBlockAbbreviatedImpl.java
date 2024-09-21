package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockAbbreviated;
import org.jetbrains.annotations.NotNull;

public class PodBlockAbbreviatedImpl extends RakuASTWrapperPsiElement implements PodBlockAbbreviated {
    public PodBlockAbbreviatedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
