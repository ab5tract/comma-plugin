package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRace;
import org.jetbrains.annotations.NotNull;

public class RakuRaceImpl extends ASTWrapperPsiElement implements RakuRace {
    public RakuRaceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
