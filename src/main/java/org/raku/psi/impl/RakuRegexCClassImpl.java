package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexCClass;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassImpl extends ASTWrapperPsiElement implements RakuRegexCClass {
    public RakuRegexCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
