package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6PostfixApplication;
import org.jetbrains.annotations.NotNull;

public class Perl6PostfixApplicationImpl extends ASTWrapperPsiElement implements Perl6PostfixApplication {
    public Perl6PostfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
