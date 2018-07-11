package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6InfixApplication;
import org.jetbrains.annotations.NotNull;

public class Perl6InfixApplicationImpl extends ASTWrapperPsiElement implements Perl6InfixApplication {
    public Perl6InfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
