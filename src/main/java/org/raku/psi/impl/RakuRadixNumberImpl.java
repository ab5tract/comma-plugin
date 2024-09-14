package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRadixNumber;
import org.jetbrains.annotations.NotNull;

public class RakuRadixNumberImpl extends ASTWrapperPsiElement implements RakuRadixNumber {
    public RakuRadixNumberImpl(@NotNull ASTNode node) {
        super(node);
    }
}
