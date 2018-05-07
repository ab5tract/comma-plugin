package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.Perl6TypeNameReference;
import org.jetbrains.annotations.NotNull;

public class Perl6TypeNameImpl extends ASTWrapperPsiElement implements Perl6TypeName {
    public Perl6TypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6TypeNameReference(this);
    }
}
