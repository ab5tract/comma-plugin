package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6BracketedInfix;
import org.jetbrains.annotations.NotNull;

public class Perl6BracketedInfixImpl extends ASTWrapperPsiElement implements Perl6BracketedInfix {
    public Perl6BracketedInfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6OpReference(this);
    }
}
