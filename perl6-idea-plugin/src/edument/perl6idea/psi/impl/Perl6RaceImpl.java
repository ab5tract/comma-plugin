package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Race;
import org.jetbrains.annotations.NotNull;

public class Perl6RaceImpl extends ASTWrapperPsiElement implements Perl6Race {
    public Perl6RaceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
