package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateParameter;
import org.jetbrains.annotations.NotNull;

public class CroTemplateParameterImpl extends ASTWrapperPsiElement implements CroTemplateParameter {
    public CroTemplateParameterImpl(@NotNull ASTNode node) {
        super(node);
    }
}
