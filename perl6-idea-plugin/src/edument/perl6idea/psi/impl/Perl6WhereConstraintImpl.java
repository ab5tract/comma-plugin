package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6WhereConstraint;
import org.jetbrains.annotations.NotNull;

public class Perl6WhereConstraintImpl extends ASTWrapperPsiElement implements Perl6WhereConstraint {
    public Perl6WhereConstraintImpl(@NotNull ASTNode node) {
        super(node);
    }
}
