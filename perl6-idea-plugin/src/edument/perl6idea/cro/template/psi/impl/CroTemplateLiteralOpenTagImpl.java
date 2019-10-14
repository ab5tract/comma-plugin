package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateLiteralOpenTag;
import org.jetbrains.annotations.NotNull;

public class CroTemplateLiteralOpenTagImpl extends ASTWrapperPsiElement implements CroTemplateLiteralOpenTag {
    public CroTemplateLiteralOpenTagImpl(@NotNull ASTNode node) {
        super(node);
    }
}
