package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Call;
import edument.perl6idea.psi.effects.Effect;
import edument.perl6idea.psi.effects.EffectCollection;
import org.jetbrains.annotations.NotNull;

public class Perl6CallImpl extends ASTWrapperPsiElement implements Perl6Call {
    public Perl6CallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull EffectCollection inferEffects() {
        return EffectCollection.of(Effect.IMPURE);
    }
}
