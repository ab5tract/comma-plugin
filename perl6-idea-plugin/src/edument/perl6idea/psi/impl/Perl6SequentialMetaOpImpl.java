package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6SequentialMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6SequentialMetaOpImpl extends ASTWrapperPsiElement implements Perl6SequentialMetaOp {
    public Perl6SequentialMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
