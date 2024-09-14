package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodPreComment;

public class PodPreCommentImpl extends ASTWrapperPsiElement implements PodPreComment {
    public PodPreCommentImpl(ASTNode node) {
        super(node);
    }
}
