package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ReduceMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6ReduceMetaOpImpl extends ASTWrapperPsiElement implements Perl6ReduceMetaOp {
    public Perl6ReduceMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
