package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexCClassElem;
import org.jetbrains.annotations.NotNull;

public class RakuRegexCClassElemImpl extends ASTWrapperPsiElement implements RakuRegexCClassElem {
    public RakuRegexCClassElemImpl(@NotNull ASTNode node) {
        super(node);
    }
}
