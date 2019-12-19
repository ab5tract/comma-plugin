package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6RegexAssertion;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexAssertionImpl extends ASTWrapperPsiElement implements Perl6RegexAssertion {
    public Perl6RegexAssertionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return "$" + getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
