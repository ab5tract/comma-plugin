package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6PrefixApplication;
import org.jetbrains.annotations.NotNull;

public class Perl6PrefixApplicationImpl extends ASTWrapperPsiElement implements Perl6PrefixApplication {
    public Perl6PrefixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
