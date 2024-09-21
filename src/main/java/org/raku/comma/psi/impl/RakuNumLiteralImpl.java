package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuNumLiteral;
import org.raku.comma.psi.effects.EffectCollection;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuNumLiteralImpl extends RakuASTWrapperPsiElement implements RakuNumLiteral {
    public RakuNumLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Num);
    }

    @Override
    @NotNull
    public EffectCollection inferEffects() {
        return EffectCollection.EMPTY;
    }
}
