package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuControlStatement;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuControlStatementImpl extends RakuASTWrapperPsiElement implements RakuControlStatement {
    public RakuControlStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public RakuType inferTopicType() {
        return lookupGlobalSymbol(RakuSettingTypeId.Exception);
    }
}
