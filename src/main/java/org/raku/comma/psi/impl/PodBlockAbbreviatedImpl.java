package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodBlockAbbreviated;
import org.jetbrains.annotations.NotNull;

public class PodBlockAbbreviatedImpl extends ASTWrapperPsiElement implements PodBlockAbbreviated {
    public PodBlockAbbreviatedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
