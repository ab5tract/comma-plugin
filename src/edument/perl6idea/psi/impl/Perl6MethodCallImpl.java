package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6MethodCall;
import edument.perl6idea.psi.Perl6MethodReference;
import org.jetbrains.annotations.NotNull;

public class Perl6MethodCallImpl extends ASTWrapperPsiElement implements Perl6MethodCall {
    public Perl6MethodCallImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6MethodReference(this);
    }
}
