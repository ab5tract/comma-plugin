package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuRegexVariableImpl extends RakuASTWrapperPsiElement implements RakuRegexVariable {
    public RakuRegexVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        RakuVariable var = PsiTreeUtil.getChildOfType(this, RakuVariable.class);
        return var == null ? "" : var.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.getChildOfType(this, RakuVariable.class);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        RakuPsiScope scope = PsiTreeUtil.getParentOfType(this, RakuBlock.class, RakuFile.class);
        if (scope != null)
            return new LocalSearchScope(scope);
        return super.getUseScope();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        RakuVariable var = PsiTreeUtil.getChildOfType(this, RakuVariable.class);
        if (var != null) {
            var.replace(RakuElementFactory.createVariable(getProject(), name));
        }
        return this;
    }
}
