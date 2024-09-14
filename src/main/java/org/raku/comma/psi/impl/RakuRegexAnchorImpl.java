package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexAnchor;
import org.jetbrains.annotations.NotNull;

public class RakuRegexAnchorImpl extends ASTWrapperPsiElement implements RakuRegexAnchor {
    public RakuRegexAnchorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
