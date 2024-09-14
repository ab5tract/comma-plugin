package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexBuiltinCClass;
import org.jetbrains.annotations.NotNull;

public class RakuRegexBuiltinCClassImpl extends ASTWrapperPsiElement implements RakuRegexBuiltinCClass {
    public RakuRegexBuiltinCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
