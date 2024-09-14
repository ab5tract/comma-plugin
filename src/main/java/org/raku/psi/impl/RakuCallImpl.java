package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuCall;
import org.raku.psi.effects.Effect;
import org.raku.psi.effects.EffectCollection;
import org.jetbrains.annotations.NotNull;

public class RakuCallImpl extends ASTWrapperPsiElement implements RakuCall {
    public RakuCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull EffectCollection inferEffects() {
        return EffectCollection.of(Effect.IMPURE);
    }
}
