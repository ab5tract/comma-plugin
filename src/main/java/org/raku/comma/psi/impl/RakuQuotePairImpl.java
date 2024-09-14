package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuQuotePair;
import org.jetbrains.annotations.NotNull;

public class RakuQuotePairImpl extends ASTWrapperPsiElement implements RakuQuotePair {
    public RakuQuotePairImpl(@NotNull ASTNode node) {
        super(node);
    }
}
