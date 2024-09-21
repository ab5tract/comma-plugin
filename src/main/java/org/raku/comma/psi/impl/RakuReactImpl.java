package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReact;
import org.jetbrains.annotations.NotNull;

public class RakuReactImpl extends RakuASTWrapperPsiElement implements RakuReact {
    public RakuReactImpl(@NotNull ASTNode node) {
        super(node);
    }
}
