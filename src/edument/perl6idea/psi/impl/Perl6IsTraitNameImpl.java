package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6IsTraitName;
import edument.perl6idea.psi.Perl6IsTraitReference;

public class Perl6IsTraitNameImpl extends ASTWrapperPsiElement implements Perl6IsTraitName {
    public Perl6IsTraitNameImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6IsTraitReference(this);
    }
}
