package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuOperatorAdverb;
import org.jetbrains.annotations.NotNull;

public class RakuOperatorAdverbImpl extends RakuASTWrapperPsiElement implements RakuOperatorAdverb {
    public RakuOperatorAdverbImpl(@NotNull ASTNode node) {
        super(node);
    }
}
