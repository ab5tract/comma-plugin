package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateApply;
import org.jetbrains.annotations.NotNull;

public class CroTemplateApplyImpl extends ASTWrapperPsiElement implements CroTemplateApply {
    public CroTemplateApplyImpl(@NotNull ASTNode node) {
        super(node);
    }
}
