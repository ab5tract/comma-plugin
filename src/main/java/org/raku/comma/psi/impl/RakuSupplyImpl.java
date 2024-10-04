package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuSupply;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuSupplyImpl extends RakuASTWrapperPsiElement implements RakuSupply {
    public RakuSupplyImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return lookupGlobalSymbol(RakuSettingTypeId.Supply);
    }
}
