package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuFatArrow;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuFatArrowImpl extends RakuASTWrapperPsiElement implements RakuFatArrow {
    public RakuFatArrowImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Pair);
    }

    @Override
    public String getKey() {
        return getFirstChild().getText();
    }

    @Override
    public PsiElement getValue() {
        return getLastChild();
    }
}
