package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateDerefHash;
import org.jetbrains.annotations.NotNull;

public class CroTemplateDerefHashImpl extends ASTWrapperPsiElement implements CroTemplateDerefHash {
    public CroTemplateDerefHashImpl(@NotNull ASTNode node) {
        super(node);
    }
}
