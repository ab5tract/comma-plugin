package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuotePair;
import org.jetbrains.annotations.NotNull;

public class RakuQuotePairImpl extends RakuASTWrapperPsiElement implements RakuQuotePair {
    public RakuQuotePairImpl(@NotNull ASTNode node) {
        super(node);
    }
}
