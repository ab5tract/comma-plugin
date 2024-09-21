package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.raku.comma.parsing.RakuTokenTypes;
import org.raku.comma.psi.RakuStrLiteral;
import org.raku.comma.psi.type.RakuType;
import org.raku.comma.sdk.RakuSdkType;
import org.raku.comma.sdk.RakuSettingTypeId;
import org.jetbrains.annotations.NotNull;

public class RakuStrLiteralImpl extends RakuASTWrapperPsiElement implements RakuStrLiteral {
    public RakuStrLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull RakuType inferType() {
        return RakuSdkType.getInstance().getCoreSettingType(getProject(), RakuSettingTypeId.Str);
    }

    @Override
    public String getStringText() {
        PsiElement opener = findChildByType(RakuTokenTypes.STRING_LITERAL_QUOTE_OPEN);
        PsiElement closer = findChildByType(RakuTokenTypes.STRING_LITERAL_QUOTE_CLOSE);
        int start = opener != null ? opener.getStartOffsetInParent() + opener.getTextLength() : 0;
        int end = closer != null ? closer.getStartOffsetInParent() : this.getTextLength();
        return getText().substring(start, end);
    }
}
