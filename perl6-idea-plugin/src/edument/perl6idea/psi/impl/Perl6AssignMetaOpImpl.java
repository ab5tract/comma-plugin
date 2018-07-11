package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6AssignMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6AssignMetaOpImpl extends ASTWrapperPsiElement implements Perl6AssignMetaOp {
    public Perl6AssignMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
