package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6Prefix;
import edument.perl6idea.psi.Perl6PrefixApplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6PrefixApplicationImpl extends ASTWrapperPsiElement implements Perl6PrefixApplication {
    public Perl6PrefixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getOperand() {
        return getLastChild();
    }

    @Nullable
    @Override
    public PsiElement getPrefix() {
        return getFirstChild();
    }

    @Override
    public boolean isAssignish() {
        PsiElement prefix = getPrefix();
        if (prefix instanceof Perl6Prefix) {
            String operator = prefix.getText();
            return operator.equals("++") || operator.equals("--") ||
                   operator.equals("⚛++") || operator.equals("⚛--");
        }
        return false;
    }
}
