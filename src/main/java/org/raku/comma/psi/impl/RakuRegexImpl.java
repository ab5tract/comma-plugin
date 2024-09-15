package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegex;
import org.jetbrains.annotations.NotNull;

public class RakuRegexImpl extends ASTWrapperPsiElement implements RakuRegex {
    public RakuRegexImpl(@NotNull ASTNode node) {
        super(node);
    }
}