package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuTransliteration;
import org.jetbrains.annotations.NotNull;

public class RakuTransliterationImpl extends RakuASTWrapperPsiElement implements RakuTransliteration {
    public RakuTransliterationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
