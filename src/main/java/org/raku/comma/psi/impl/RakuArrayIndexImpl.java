package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuArrayIndex;
import org.jetbrains.annotations.NotNull;

public class RakuArrayIndexImpl extends RakuASTWrapperPsiElement implements RakuArrayIndex {
    public RakuArrayIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
