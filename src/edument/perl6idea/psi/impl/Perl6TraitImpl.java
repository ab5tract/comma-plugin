package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Trait;
import org.jetbrains.annotations.NotNull;

import static edument.perl6idea.parsing.Perl6ElementTypes.LONG_NAME;
import static edument.perl6idea.parsing.Perl6ElementTypes.TRAIT;

public class Perl6TraitImpl extends ASTWrapperPsiElement implements Perl6Trait {
    public Perl6TraitImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getTraitModifier() {
        PsiElement trait = findChildByType(TRAIT);
        return trait == null ? "" : trait.getText();
    }

    @Override
    public String getTraitName() {
        PsiElement name = findChildByType(LONG_NAME);
        return name == null ? "" : name.getText();
    }
}
