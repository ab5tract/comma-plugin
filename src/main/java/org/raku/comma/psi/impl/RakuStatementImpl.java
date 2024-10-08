package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuStatement;
import org.raku.comma.psi.effects.EffectCollection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RakuStatementImpl extends RakuASTWrapperPsiElement implements RakuStatement {
    public RakuStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull EffectCollection inferEffects() {
        return Arrays.stream(getChildren())
            .filter(c -> c instanceof RakuPsiElement)
            .map(c -> ((RakuPsiElement)c).inferEffects())
            .reduce(EffectCollection.EMPTY, EffectCollection::merge);
    }
}
