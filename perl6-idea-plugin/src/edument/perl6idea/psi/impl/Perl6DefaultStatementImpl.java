package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6DefaultStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6DefaultStatementImpl extends ASTWrapperPsiElement implements Perl6DefaultStatement {
    public Perl6DefaultStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
