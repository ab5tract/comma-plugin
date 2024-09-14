package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexModInternal;
import org.jetbrains.annotations.NotNull;

public class RakuRegexModInternalImpl extends ASTWrapperPsiElement implements RakuRegexModInternal {
    public RakuRegexModInternalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
