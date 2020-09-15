package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.Perl6SubCallReference;
import org.jetbrains.annotations.NotNull;

public class Perl6SubCallNameImpl extends ASTWrapperPsiElement implements Perl6SubCallName {
    public Perl6SubCallNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        PsiElement parent = getParent();
        boolean maybeCoercion = parent instanceof Perl6SubCall && ((Perl6SubCall)parent).maybeCoercion();
        return new Perl6SubCallReference(this, maybeCoercion);
    }

    @NotNull
    @Override
    public String getCallName() {
        return getText();
    }
}
