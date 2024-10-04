package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuComplexLiteral;
import org.raku.comma.psi.effects.EffectCollection;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuComplexLiteralImpl extends RakuASTWrapperPsiElement implements RakuComplexLiteral {
    public RakuComplexLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return lookupGlobalSymbol(RakuSettingTypeId.Complex);
    }

    @Override
    @NotNull
    public EffectCollection inferEffects() {
        return EffectCollection.EMPTY;
    }
}
