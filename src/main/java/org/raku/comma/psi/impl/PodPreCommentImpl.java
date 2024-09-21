package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodPreComment;

public class PodPreCommentImpl extends RakuASTWrapperPsiElement implements PodPreComment {
    public PodPreCommentImpl(ASTNode node) {
        super(node);
    }
}
