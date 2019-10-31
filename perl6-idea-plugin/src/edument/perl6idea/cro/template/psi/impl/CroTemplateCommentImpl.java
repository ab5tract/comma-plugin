package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateComment;
import org.jetbrains.annotations.NotNull;

public class CroTemplateCommentImpl extends ASTWrapperPsiElement implements CroTemplateComment {
    public CroTemplateCommentImpl(@NotNull ASTNode node) {
        super(node);
    }
}
