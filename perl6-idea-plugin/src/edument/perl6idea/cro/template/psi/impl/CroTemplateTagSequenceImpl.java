package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateTagSequence;
import org.jetbrains.annotations.NotNull;

public class CroTemplateTagSequenceImpl extends ASTWrapperPsiElement implements CroTemplateTagSequence {
    public CroTemplateTagSequenceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
