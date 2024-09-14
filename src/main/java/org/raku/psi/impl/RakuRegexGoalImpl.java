package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexGoal;
import org.jetbrains.annotations.NotNull;

public class RakuRegexGoalImpl extends ASTWrapperPsiElement implements RakuRegexGoal {
    public RakuRegexGoalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
