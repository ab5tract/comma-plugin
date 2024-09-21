package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuOperatorAdverbApplication;
import org.jetbrains.annotations.NotNull;

public class RakuOperatorAdverbApplicationImpl extends RakuASTWrapperPsiElement implements RakuOperatorAdverbApplication {
    public RakuOperatorAdverbApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
