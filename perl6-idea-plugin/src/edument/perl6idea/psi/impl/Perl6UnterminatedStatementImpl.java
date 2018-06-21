package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6UnterminatedStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6UnterminatedStatementImpl extends ASTWrapperPsiElement implements Perl6UnterminatedStatement {
    public Perl6UnterminatedStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
