package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateLiteralTagAttribute;
import org.jetbrains.annotations.NotNull;

public class CroTemplateLiteralTagAttributeImpl extends ASTWrapperPsiElement implements CroTemplateLiteralTagAttribute {
    public CroTemplateLiteralTagAttributeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
