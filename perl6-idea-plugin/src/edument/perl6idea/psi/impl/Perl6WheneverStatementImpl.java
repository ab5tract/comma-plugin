package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6WheneverStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6WheneverStatementImpl extends ASTWrapperPsiElement implements Perl6WheneverStatement {
    public Perl6WheneverStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
