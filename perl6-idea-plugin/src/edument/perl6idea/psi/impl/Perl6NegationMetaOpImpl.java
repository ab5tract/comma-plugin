package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6NegationMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6NegationMetaOpImpl extends ASTWrapperPsiElement implements Perl6NegationMetaOp {
    public Perl6NegationMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
