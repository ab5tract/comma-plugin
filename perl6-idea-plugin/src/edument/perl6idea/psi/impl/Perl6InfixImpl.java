package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6Infix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6InfixImpl extends ASTWrapperPsiElement implements Perl6Infix {
    public Perl6InfixImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getLeftSide() {
        return skipWhitespacesBackward();
    }

    @Nullable
    @Override
    public PsiElement getRightSide() {
        return skipWhitespacesForward();
    }

    @Override
    public PsiElement getOperator() {
        return findChildByType(TokenSet.create(Perl6TokenTypes.INFIX));
    }

    @Override
    public PsiReference getReference() {
        return new Perl6OpReference(this);
    }
}
