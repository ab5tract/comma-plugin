package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6Postfix;
import org.jetbrains.annotations.NotNull;

public class Perl6PostfixImpl extends ASTWrapperPsiElement implements Perl6Postfix {
    public Perl6PostfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6OpReference(this);
    }
}
