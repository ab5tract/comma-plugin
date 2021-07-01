package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Self;
import edument.perl6idea.psi.Perl6SelfReference;
import edument.perl6idea.psi.type.Perl6SelfType;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
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
    public @NotNull Perl6Type inferType() {
        Perl6PackageDecl packageDecl = PsiTreeUtil.getParentOfType(this, Perl6PackageDecl.class);
        return packageDecl != null
                ? new Perl6SelfType(packageDecl)
                : Perl6Untyped.INSTANCE;
    }
}
