package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexLiteral;
import org.jetbrains.annotations.NotNull;

public class RakuRegexLiteralImpl extends RakuASTWrapperPsiElement implements RakuRegexLiteral {
    public RakuRegexLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return getTextLength() == 0;
    }
}
