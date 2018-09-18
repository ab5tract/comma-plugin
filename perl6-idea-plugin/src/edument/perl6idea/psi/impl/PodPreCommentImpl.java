package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.PodPreComment;

public class PodPreCommentImpl extends ASTWrapperPsiElement implements PodPreComment {
    public PodPreCommentImpl(ASTNode node) {
        super(node);
    }
}
