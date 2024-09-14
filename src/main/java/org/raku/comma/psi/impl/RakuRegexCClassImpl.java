package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexCClass;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassImpl extends ASTWrapperPsiElement implements RakuRegexCClass {
    public RakuRegexCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
