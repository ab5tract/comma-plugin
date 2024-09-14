package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexSeparator;
import org.jetbrains.annotations.NotNull;

public class RakuRegexSeparatorImpl extends ASTWrapperPsiElement implements RakuRegexSeparator {
    public RakuRegexSeparatorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
