package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuComplexLiteral;
import org.raku.psi.effects.EffectCollection;
import org.raku.psi.type.RakuType;
import org.raku.sdk.RakuSdkType;
import org.raku.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuComplexLiteralImpl extends ASTWrapperPsiElement implements RakuComplexLiteral {
    public RakuComplexLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Complex);
    }

    @Override
    @NotNull
    public EffectCollection inferEffects() {
        return EffectCollection.EMPTY;
    }
}
