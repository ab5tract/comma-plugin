package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexGoal;
import org.jetbrains.annotations.NotNull;

public class RakuRegexGoalImpl extends RakuASTWrapperPsiElement implements RakuRegexGoal {
    public RakuRegexGoalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
