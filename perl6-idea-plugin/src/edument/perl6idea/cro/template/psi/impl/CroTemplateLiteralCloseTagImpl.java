package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateLiteralCloseTag;
import org.jetbrains.annotations.NotNull;

public class CroTemplateLiteralCloseTagImpl extends ASTWrapperPsiElement implements CroTemplateLiteralCloseTag {
    public CroTemplateLiteralCloseTagImpl(@NotNull ASTNode node) {
        super(node);
    }
}
