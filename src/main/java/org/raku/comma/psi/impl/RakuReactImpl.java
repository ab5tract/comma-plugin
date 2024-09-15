package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuReact;
import org.jetbrains.annotations.NotNull;

public class RakuReactImpl extends ASTWrapperPsiElement implements RakuReact {
    public RakuReactImpl(@NotNull ASTNode node) {
        super(node);
    }
}