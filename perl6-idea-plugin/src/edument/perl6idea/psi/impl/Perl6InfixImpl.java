package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
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
    public String getOperator() {
        return getText();
    }
}
