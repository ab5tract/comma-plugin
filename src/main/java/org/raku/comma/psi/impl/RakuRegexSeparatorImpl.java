package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexSeparator;
import org.jetbrains.annotations.NotNull;

public class RakuRegexSeparatorImpl extends RakuASTWrapperPsiElement implements RakuRegexSeparator {
    public RakuRegexSeparatorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
