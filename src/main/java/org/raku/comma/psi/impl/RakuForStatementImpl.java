package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuForStatement;
import org.raku.comma.psi.RakuPointyBlock;
import org.raku.comma.psi.RakuPsiElement;
import org.raku.comma.psi.type.RakuParametricType;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.psi.type.RakuUntyped;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.raku.comma.utils.RakuPsiUtil;
import org.jetbrains.annotations.NotNull;

public class RakuForStatementImpl extends RakuASTWrapperPsiElement implements RakuForStatement {
    public RakuForStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isTopicalizing() {
        return PsiTreeUtil.getChildOfType(this, RakuPointyBlock.class) == null;
    }

    @Override
    public RakuPsiElement getSource() {
        PsiElement found = RakuPsiUtil.skipSpaces(getFirstChild().getNextSibling(), true);
        return found instanceof RakuPsiElement ? (RakuPsiElement)found : null;
    }

    @Override
    public RakuType inferLoopParameterType(int index) {
        // For now we only do array types, so this shall do.
        return inferTopicType();
    }

    @Override
    public RakuType inferTopicType() {
        RakuPsiElement source = getSource();
        if (source == null)
            return RakuUntyped.INSTANCE;

        RakuType sourceType = source.inferType().nominalType();
        if (sourceType instanceof RakuParametricType) {
            RakuType baseType = ((RakuParametricType)sourceType).getGenericType();
            if (isArrayType(baseType)) {
                RakuType[] args = ((RakuParametricType)sourceType).getArguments();
                if (args.length == 1)
                    return args[0];
            }
        }
        else if (isHashType(sourceType)) {
            return lookupGlobalSymbol(RakuSettingTypeId.Pair);
        }

        return RakuUntyped.INSTANCE;
    }

    private boolean isArrayType(RakuType type) {
        RakuType array = lookupGlobalSymbol(RakuSettingTypeId.Array);
        if (type.equals(array)) return true;
        RakuType list = lookupGlobalSymbol(RakuSettingTypeId.List);
        if (type.equals(list)) return true;
        RakuType positional = lookupGlobalSymbol(RakuSettingTypeId.Positional);
        return type.equals(positional);
    }

    private boolean isHashType(RakuType type) {
        RakuType array = lookupGlobalSymbol(RakuSettingTypeId.Hash);
        if (type.equals(array)) return true;
        RakuType list = lookupGlobalSymbol(RakuSettingTypeId.Map);
        if (type.equals(list)) return true;
        RakuType positional =lookupGlobalSymbol(RakuSettingTypeId.Associative);
        return type.equals(positional);
    }
}
