package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuQuoteRegex;
import org.raku.psi.type.RakuType;
import org.raku.sdk.RakuSdkType;
import org.raku.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuQuoteRegexImpl extends ASTWrapperPsiElement implements RakuQuoteRegex {
    public RakuQuoteRegexImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Regex);
    }
}
