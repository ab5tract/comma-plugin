package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6Self;
import edument.perl6idea.psi.Perl6SelfReference;
import org.jetbrains.annotations.NotNull;

public class Perl6SelfImpl extends ASTWrapperPsiElement implements Perl6Self {
    public Perl6SelfImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6SelfReference(this);
    }

    @Override
    public @NotNull String inferType() {
        return "self";
    }
}
