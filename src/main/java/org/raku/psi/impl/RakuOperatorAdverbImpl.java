package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuOperatorAdverb;
import org.jetbrains.annotations.NotNull;

public class RakuOperatorAdverbImpl extends ASTWrapperPsiElement implements RakuOperatorAdverb {
    public RakuOperatorAdverbImpl(@NotNull ASTNode node) {
        super(node);
    }
}
