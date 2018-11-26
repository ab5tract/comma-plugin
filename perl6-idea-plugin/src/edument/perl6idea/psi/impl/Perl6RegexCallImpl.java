package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6RegexCall;
import edument.perl6idea.psi.Perl6RegexCallReference;

public class Perl6RegexCallImpl extends ASTWrapperPsiElement implements Perl6RegexCall {
    public Perl6RegexCallImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6RegexCallReference(this);
    }
}
