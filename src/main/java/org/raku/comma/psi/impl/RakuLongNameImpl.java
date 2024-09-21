package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuColonPair;
import org.raku.comma.psi.RakuLongName;
import org.jetbrains.annotations.NotNull;

public class RakuLongNameImpl extends RakuASTWrapperPsiElement implements RakuLongName {
    public RakuLongNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getNameWithoutColonPairs() {
        RakuColonPair firstColonPair = findChildByClass(RakuColonPair.class);
        return firstColonPair == null
               ? getText()
               : getText().substring(0, firstColonPair.getStartOffsetInParent());
    }

    @Override
    @NotNull
    public RakuColonPair[] getColonPairs() {
        return findChildrenByClass(RakuColonPair.class);
    }
}
