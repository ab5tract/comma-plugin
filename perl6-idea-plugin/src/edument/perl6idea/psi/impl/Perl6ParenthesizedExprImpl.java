package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6Untyped;
import edument.perl6idea.sdk.Perl6SdkType;
import edument.perl6idea.sdk.Perl6SettingTypeId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class Perl6ParenthesizedExprImpl extends ASTWrapperPsiElement implements Perl6ParenthesizedExpr {
    public Perl6ParenthesizedExprImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull Perl6Type inferType() {
        Collection<Perl6Statement> children = PsiTreeUtil.findChildrenOfType(this, Perl6Statement.class);
        ArrayList<Perl6Statement> list = new ArrayList<>(children);
        if (list.size() == 0)
            return Perl6SdkType.getInstance().getCoreSettingType(getProject(), Perl6SettingTypeId.List);
        if (list.size() == 1 ||
            (list.size() == 2 && PsiTreeUtil.isAncestor(list.get(0), list.get(1), true))) {
            Perl6PsiElement firstChild = (Perl6PsiElement)list.get(0).getFirstChild();
            return firstChild.inferType();
        }
        return Perl6Untyped.INSTANCE;
    }
}
