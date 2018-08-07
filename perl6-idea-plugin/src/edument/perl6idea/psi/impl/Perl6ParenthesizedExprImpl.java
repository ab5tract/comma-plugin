package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Perl6ParenthesizedExprImpl extends ASTWrapperPsiElement implements Perl6ParenthesizedExpr {
    public Perl6ParenthesizedExprImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        Collection<Perl6Statement> children = PsiTreeUtil.findChildrenOfType(this, Perl6Statement.class);
        ArrayList<Perl6Statement> list = new ArrayList<>(children);
        if (list.size() == 0) return "List";
        if (list.size() == 1 ||
            (list.size() == 2 && PsiTreeUtil.isAncestor(list.get(0), list.get(1), true))) {
            Perl6PsiElement firstChild = (Perl6PsiElement)list.get(0).getFirstChild();
            return firstChild.inferType();
        }
        return "Any";
    }
}
