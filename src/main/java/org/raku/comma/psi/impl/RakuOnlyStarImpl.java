package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuOnlyStar;
import org.jetbrains.annotations.NotNull;

public class RakuOnlyStarImpl extends RakuASTWrapperPsiElement implements RakuOnlyStar {
    public RakuOnlyStarImpl(@NotNull ASTNode node) {
        super(node);
    }
}
