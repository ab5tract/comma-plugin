package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodPostComment;

public class PodPostCommentImpl extends ASTWrapperPsiElement implements PodPostComment {
    public PodPostCommentImpl(ASTNode node) {
        super(node);
    }
}
