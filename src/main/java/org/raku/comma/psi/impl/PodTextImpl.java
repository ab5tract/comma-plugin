package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodText;
import org.jetbrains.annotations.NotNull;

public class PodTextImpl extends ASTWrapperPsiElement implements PodText {
    public PodTextImpl(@NotNull ASTNode node) {
        super(node);
    }
}
