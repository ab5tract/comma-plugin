package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ReverseMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6ReverseMetaOpImpl extends ASTWrapperPsiElement implements Perl6ReverseMetaOp {
    public Perl6ReverseMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
