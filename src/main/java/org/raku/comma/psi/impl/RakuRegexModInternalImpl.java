package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexModInternal;
import org.jetbrains.annotations.NotNull;

public class RakuRegexModInternalImpl extends RakuASTWrapperPsiElement implements RakuRegexModInternal {
    public RakuRegexModInternalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
