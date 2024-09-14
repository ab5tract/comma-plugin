package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuHeredoc;
import org.jetbrains.annotations.NotNull;

public class RakuHeredocImpl extends ASTWrapperPsiElement implements RakuHeredoc {
    public RakuHeredocImpl(@NotNull ASTNode node) {
        super(node);
    }
}
