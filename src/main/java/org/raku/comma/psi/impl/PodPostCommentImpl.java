package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodPostComment;

public class PodPostCommentImpl extends RakuASTWrapperPsiElement implements PodPostComment {
    public PodPostCommentImpl(ASTNode node) {
        super(node);
    }
}
