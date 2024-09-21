package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexBuiltinCClass;
import org.jetbrains.annotations.NotNull;

public class RakuRegexBuiltinCClassImpl extends RakuASTWrapperPsiElement implements RakuRegexBuiltinCClass {
    public RakuRegexBuiltinCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
