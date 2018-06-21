package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6UnlessStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6UnlessStatementImpl extends ASTWrapperPsiElement implements Perl6UnlessStatement {
    public Perl6UnlessStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
