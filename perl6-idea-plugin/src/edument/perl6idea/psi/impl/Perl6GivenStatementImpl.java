package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6GivenStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6GivenStatementImpl extends ASTWrapperPsiElement implements Perl6GivenStatement {
    public Perl6GivenStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
