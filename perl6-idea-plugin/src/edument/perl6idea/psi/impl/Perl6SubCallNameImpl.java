package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6SubCallName;
import edument.perl6idea.psi.Perl6SubCallReference;
import org.jetbrains.annotations.NotNull;

public class Perl6SubCallNameImpl extends ASTWrapperPsiElement implements Perl6SubCallName {
    public Perl6SubCallNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6SubCallReference(this);
    }

    @Override
    public String getCallName() {
        return this.getText();
    }
}
