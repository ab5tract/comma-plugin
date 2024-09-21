package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexCClassElem;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassElemImpl extends RakuASTWrapperPsiElement implements RakuRegexCClassElem {
    public RakuRegexCClassElemImpl(@NotNull ASTNode node) {
        super(node);
    }
}
