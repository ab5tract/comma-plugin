package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuIntLiteral;
import org.raku.psi.effects.EffectCollection;
import org.raku.psi.type.RakuType;
import org.raku.sdk.RakuSdkType;
import org.raku.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuIntLiteralImpl extends ASTWrapperPsiElement implements RakuIntLiteral {
    public RakuIntLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Int);
    }

    @Override
    @NotNull
    public EffectCollection inferEffects() {
        return EffectCollection.EMPTY;
    }
}
