package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Comment;
import org.jetbrains.annotations.NotNull;

public class Perl6CommentImpl extends ASTWrapperPsiElement implements Perl6Comment {
    public Perl6CommentImpl(@NotNull ASTNode node) {
        super(node);
    }
}
