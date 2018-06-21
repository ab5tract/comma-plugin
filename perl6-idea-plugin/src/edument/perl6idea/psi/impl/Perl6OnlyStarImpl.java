package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6OnlyStar;
import org.jetbrains.annotations.NotNull;

public class Perl6OnlyStarImpl extends ASTWrapperPsiElement implements Perl6OnlyStar {
    public Perl6OnlyStarImpl(@NotNull ASTNode node) {
        super(node);
    }
}
