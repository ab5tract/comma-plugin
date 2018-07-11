package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ReturnConstraint;
import org.jetbrains.annotations.NotNull;

public class Perl6ReturnConstraintImpl extends ASTWrapperPsiElement implements Perl6ReturnConstraint {
    public Perl6ReturnConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
