package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexCClassElem;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassElemImpl extends ASTWrapperPsiElement implements RakuRegexCClassElem {
    public RakuRegexCClassElemImpl(@NotNull ASTNode node) {
        super(node);
    }
}
