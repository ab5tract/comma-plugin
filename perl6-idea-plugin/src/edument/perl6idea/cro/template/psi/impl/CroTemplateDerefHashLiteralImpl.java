package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateDerefHashLiteral;
import org.jetbrains.annotations.NotNull;

public class CroTemplateDerefHashLiteralImpl extends ASTWrapperPsiElement implements CroTemplateDerefHashLiteral {
    public CroTemplateDerefHashLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }
}
