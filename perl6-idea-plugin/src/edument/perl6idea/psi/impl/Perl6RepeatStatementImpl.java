package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RepeatStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6RepeatStatementImpl extends ASTWrapperPsiElement implements Perl6RepeatStatement {
    public Perl6RepeatStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
