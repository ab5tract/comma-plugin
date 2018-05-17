package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6ModuleName;
import edument.perl6idea.psi.Perl6ModuleReference;
import org.jetbrains.annotations.NotNull;

public class Perl6ModuleNameImpl extends ASTWrapperPsiElement implements Perl6ModuleName {
    public Perl6ModuleNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6ModuleReference(this);
    }
}
