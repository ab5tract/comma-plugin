package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6RegexVariable;
import edument.perl6idea.psi.Perl6Variable;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexVariableImpl extends ASTWrapperPsiElement implements Perl6RegexVariable {
    public Perl6RegexVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        Perl6Variable var = PsiTreeUtil.getChildOfType(this, Perl6Variable.class);
        return var == null ? "" : var.getText();
    }

    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
}
