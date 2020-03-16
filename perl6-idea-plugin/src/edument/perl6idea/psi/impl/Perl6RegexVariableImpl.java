package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
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

    @NotNull
    @Override
    public SearchScope getUseScope() {
        Perl6PsiScope scope = PsiTreeUtil.getParentOfType(this, Perl6Block.class, Perl6File.class);
        if (scope != null)
            return new LocalSearchScope(scope);
        return super.getUseScope();
    }

    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6Variable var = PsiTreeUtil.getChildOfType(this, Perl6Variable.class);
        if (var != null) {
            var.replace(Perl6ElementFactory.createVariable(getProject(), name));
        }
        return this;
    }
}
