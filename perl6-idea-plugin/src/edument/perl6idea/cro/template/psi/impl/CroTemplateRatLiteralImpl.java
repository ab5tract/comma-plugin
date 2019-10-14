package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateRatLiteral;
import org.jetbrains.annotations.NotNull;

public class CroTemplateRatLiteralImpl extends ASTWrapperPsiElement implements CroTemplateRatLiteral {
    public CroTemplateRatLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }
}
