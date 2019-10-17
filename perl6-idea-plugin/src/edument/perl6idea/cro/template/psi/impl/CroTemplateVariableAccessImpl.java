package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateVariableAccess;
import org.jetbrains.annotations.NotNull;

public class CroTemplateVariableAccessImpl extends ASTWrapperPsiElement implements CroTemplateVariableAccess {
    public CroTemplateVariableAccessImpl(@NotNull ASTNode node) {
        super(node);
    }
}
