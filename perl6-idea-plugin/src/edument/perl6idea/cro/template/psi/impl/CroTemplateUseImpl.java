package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateUse;
import org.jetbrains.annotations.NotNull;

public class CroTemplateUseImpl extends ASTWrapperPsiElement implements CroTemplateUse {
    public CroTemplateUseImpl(@NotNull ASTNode node) {
        super(node);
    }
}
