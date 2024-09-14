package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuCatchStatement;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuCatchStatementImpl extends ASTWrapperPsiElement implements RakuCatchStatement {
    public RakuCatchStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public RakuType inferTopicType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Exception);
    }
}
