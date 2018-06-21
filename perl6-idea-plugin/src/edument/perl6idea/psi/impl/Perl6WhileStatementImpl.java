package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6WhileStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6WhileStatementImpl extends ASTWrapperPsiElement implements Perl6WhileStatement {
    public Perl6WhileStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
