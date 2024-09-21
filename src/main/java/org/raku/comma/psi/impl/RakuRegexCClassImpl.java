package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexCClass;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassImpl extends RakuASTWrapperPsiElement implements RakuRegexCClass {
    public RakuRegexCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
