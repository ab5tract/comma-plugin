package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.PodPostComment;

public class PodPostCommentImpl extends ASTWrapperPsiElement implements PodPostComment {
    public PodPostCommentImpl(ASTNode node) {
        super(node);
    }
}
