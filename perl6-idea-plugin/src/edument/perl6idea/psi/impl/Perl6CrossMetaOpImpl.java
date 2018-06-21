package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6CrossMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6CrossMetaOpImpl extends ASTWrapperPsiElement implements Perl6CrossMetaOp {
    public Perl6CrossMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
