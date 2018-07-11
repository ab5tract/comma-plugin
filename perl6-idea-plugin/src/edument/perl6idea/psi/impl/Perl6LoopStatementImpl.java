package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6LoopStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6LoopStatementImpl extends ASTWrapperPsiElement implements Perl6LoopStatement {
    public Perl6LoopStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
