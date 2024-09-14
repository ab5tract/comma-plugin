package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexGoal;
import org.jetbrains.annotations.NotNull;

public class RakuRegexGoalImpl extends ASTWrapperPsiElement implements RakuRegexGoal {
    public RakuRegexGoalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
