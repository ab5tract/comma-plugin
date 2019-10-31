package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateCall;
import org.jetbrains.annotations.NotNull;

public class CroTemplateCallImpl extends ASTWrapperPsiElement implements CroTemplateCall {
    public CroTemplateCallImpl(@NotNull ASTNode node) {
        super(node);
    }
}
