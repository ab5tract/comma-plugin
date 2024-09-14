package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.psi.RakuFatArrow;
import org.raku.psi.type.RakuType;
import org.raku.sdk.RakuSdkType;
import org.raku.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuFatArrowImpl extends ASTWrapperPsiElement implements RakuFatArrow {
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
