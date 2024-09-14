package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuComment;
import org.jetbrains.annotations.NotNull;

public class RakuCommentImpl extends ASTWrapperPsiElement implements RakuComment {
    public RakuCommentImpl(@NotNull ASTNode node) {
        super(node);
    }
}
