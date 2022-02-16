package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateSeparator;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSeparatorImpl extends ASTWrapperPsiElement implements CroTemplateSeparator {
    public CroTemplateSeparatorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
