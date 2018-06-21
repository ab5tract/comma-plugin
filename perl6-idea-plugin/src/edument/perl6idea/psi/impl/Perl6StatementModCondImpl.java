package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6StatementModCond;
import org.jetbrains.annotations.NotNull;

public class Perl6StatementModCondImpl extends ASTWrapperPsiElement implements Perl6StatementModCond {
    public Perl6StatementModCondImpl(@NotNull ASTNode node) {
        super(node);
    }
}
