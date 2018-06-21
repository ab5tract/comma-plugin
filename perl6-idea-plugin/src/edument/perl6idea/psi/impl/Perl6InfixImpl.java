package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Infix;
import org.jetbrains.annotations.NotNull;

public class Perl6InfixImpl extends ASTWrapperPsiElement implements Perl6Infix {
    public Perl6InfixImpl(@NotNull ASTNode node) {
        super(node);
    }
}
