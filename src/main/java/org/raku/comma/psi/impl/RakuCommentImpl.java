package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuComment;
import org.jetbrains.annotations.NotNull;

public class RakuCommentImpl extends RakuASTWrapperPsiElement implements RakuComment {
    public RakuCommentImpl(@NotNull ASTNode node) {
        super(node);
    }
}
