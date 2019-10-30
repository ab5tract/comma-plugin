package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateSignature;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSignatureImpl extends ASTWrapperPsiElement implements CroTemplateSignature {
    public CroTemplateSignatureImpl(@NotNull ASTNode node) {
        super(node);
    }
}
