package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Trusts;

public class Perl6TrustsImpl extends ASTWrapperPsiElement implements Perl6Trusts {
    public Perl6TrustsImpl(ASTNode node) {
        super(node);
    }
}
