package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ForStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6ForStatementImpl extends ASTWrapperPsiElement implements Perl6ForStatement {
    public Perl6ForStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
