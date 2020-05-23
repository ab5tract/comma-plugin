package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6CatchStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6CatchStatementImpl extends ASTWrapperPsiElement implements Perl6CatchStatement {
    public Perl6CatchStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferTopicType() { return "Exception"; }
}
