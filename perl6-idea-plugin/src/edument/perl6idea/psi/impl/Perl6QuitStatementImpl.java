package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6QuitStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6QuitStatementImpl extends ASTWrapperPsiElement implements Perl6QuitStatement {
    public Perl6QuitStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
