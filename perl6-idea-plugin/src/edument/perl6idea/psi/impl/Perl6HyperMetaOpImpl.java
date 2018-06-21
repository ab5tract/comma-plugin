package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6HyperMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6HyperMetaOpImpl extends ASTWrapperPsiElement implements Perl6HyperMetaOp {
    public Perl6HyperMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
