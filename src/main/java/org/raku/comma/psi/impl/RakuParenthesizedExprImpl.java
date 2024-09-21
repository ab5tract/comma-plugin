package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuParenthesizedExpr;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.RakuStatement;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RakuParenthesizedExprImpl extends RakuASTWrapperPsiElement implements RakuParenthesizedExpr {
    public RakuParenthesizedExprImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        Collection<RakuStatement> children = PsiTreeUtil.findChildrenOfType(this, RakuStatement.class);
        ArrayList<RakuStatement> list = new ArrayList<>(children);
        if (list.size() == 0)
            return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.List);
        if (list.size() == 1 ||
            (list.size() == 2 && PsiTreeUtil.isAncestor(list.get(0), list.get(1), true))) {
            RakuPsiElement firstChild = (RakuPsiElement)list.get(0).getFirstChild();
            return firstChild.inferType();
        }
        return RakuUntyped.INSTANCE;
    }
}
