package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSubImpl extends ASTWrapperPsiElement implements CroTemplateSub {
    public CroTemplateSubImpl(@NotNull ASTNode node) {
        super(node);
    }
}
