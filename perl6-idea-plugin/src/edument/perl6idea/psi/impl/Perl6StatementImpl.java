package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6Statement;
import edument.perl6idea.psi.effects.EffectCollection;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Perl6StatementImpl extends ASTWrapperPsiElement implements Perl6Statement {
    public Perl6StatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull EffectCollection inferEffects() {
        return Arrays.stream(getChildren())
            .filter(c -> c instanceof Perl6PsiElement)
            .map(c -> ((Perl6PsiElement)c).inferEffects())
            .reduce(EffectCollection.EMPTY, EffectCollection::merge);
    }
}
