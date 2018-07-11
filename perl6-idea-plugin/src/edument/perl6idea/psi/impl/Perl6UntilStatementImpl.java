package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6UntilStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6UntilStatementImpl extends ASTWrapperPsiElement implements Perl6UntilStatement {
    public Perl6UntilStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
