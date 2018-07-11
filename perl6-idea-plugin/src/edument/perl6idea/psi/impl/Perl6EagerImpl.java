package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Eager;
import org.jetbrains.annotations.NotNull;

public class Perl6EagerImpl extends ASTWrapperPsiElement implements Perl6Eager {
    public Perl6EagerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
