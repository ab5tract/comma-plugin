package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHeredoc;
import org.jetbrains.annotations.NotNull;

public class RakuHeredocImpl extends RakuASTWrapperPsiElement implements RakuHeredoc {
    public RakuHeredocImpl(@NotNull ASTNode node) {
        super(node);
    }
}
