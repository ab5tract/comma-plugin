package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6WhenStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6WhenStatementImpl extends ASTWrapperPsiElement implements Perl6WhenStatement {
    public Perl6WhenStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
