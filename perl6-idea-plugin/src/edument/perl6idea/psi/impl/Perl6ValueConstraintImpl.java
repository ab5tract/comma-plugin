package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ValueConstraint;
import org.jetbrains.annotations.NotNull;

public class Perl6ValueConstraintImpl extends ASTWrapperPsiElement implements Perl6ValueConstraint {
    public Perl6ValueConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
