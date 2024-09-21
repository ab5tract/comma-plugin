package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegex;
import org.jetbrains.annotations.NotNull;

public class RakuRegexImpl extends RakuASTWrapperPsiElement implements RakuRegex {
    public RakuRegexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
