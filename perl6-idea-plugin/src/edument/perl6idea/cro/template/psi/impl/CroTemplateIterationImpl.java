package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateIteration;
import org.jetbrains.annotations.NotNull;

public class CroTemplateIterationImpl extends ASTWrapperPsiElement implements CroTemplateIteration {
    public CroTemplateIterationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
