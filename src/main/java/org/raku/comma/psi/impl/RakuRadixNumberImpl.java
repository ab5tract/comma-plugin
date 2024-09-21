package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRadixNumber;
import org.jetbrains.annotations.NotNull;

public class RakuRadixNumberImpl extends RakuASTWrapperPsiElement implements RakuRadixNumber {
    public RakuRadixNumberImpl(@NotNull ASTNode node) {
        super(node);
    }
}
