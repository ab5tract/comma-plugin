package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexAnchor;
import org.jetbrains.annotations.NotNull;

public class RakuRegexAnchorImpl extends RakuASTWrapperPsiElement implements RakuRegexAnchor {
    public RakuRegexAnchorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
