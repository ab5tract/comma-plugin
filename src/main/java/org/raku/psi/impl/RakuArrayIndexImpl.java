package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuArrayIndex;
import org.jetbrains.annotations.NotNull;

public class RakuArrayIndexImpl extends ASTWrapperPsiElement implements RakuArrayIndex {
    public RakuArrayIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
