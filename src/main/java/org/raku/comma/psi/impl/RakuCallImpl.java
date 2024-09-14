package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuCall;
import org.raku.comma.psi.effects.Effect;
import org.raku.comma.psi.effects.EffectCollection;
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
