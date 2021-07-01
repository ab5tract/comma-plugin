package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.PodBlockParagraph;
import org.jetbrains.annotations.NotNull;

public class PodBlockParagraphImpl extends ASTWrapperPsiElement implements PodBlockParagraph {
    public PodBlockParagraphImpl(@NotNull ASTNode node) {
        super(node);
    }

}
