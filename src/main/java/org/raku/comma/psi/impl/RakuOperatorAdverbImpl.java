package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuOperatorAdverb;
import org.jetbrains.annotations.NotNull;

public class RakuOperatorAdverbImpl extends ASTWrapperPsiElement implements RakuOperatorAdverb {
    public RakuOperatorAdverbImpl(@NotNull ASTNode node) {
        super(node);
    }
}
