package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6NoStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6NoStatementImpl extends ASTWrapperPsiElement implements Perl6NoStatement {
    public Perl6NoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
