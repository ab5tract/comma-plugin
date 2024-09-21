package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRace;
import org.jetbrains.annotations.NotNull;

public class RakuRaceImpl extends RakuASTWrapperPsiElement implements RakuRace {
    public RakuRaceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
