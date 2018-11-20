package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6IsTraitName;
import edument.perl6idea.psi.Perl6IsTraitReference;
import edument.perl6idea.psi.Perl6LongName;
import org.jetbrains.annotations.NotNull;

public class Perl6IsTraitNameImpl extends ASTWrapperPsiElement implements Perl6IsTraitName {
    public Perl6IsTraitNameImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6IsTraitReference(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6LongName type = Perl6ElementFactory
            .createIsTraitName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = type.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
