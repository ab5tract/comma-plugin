package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import org.jetbrains.annotations.NotNull;

public class CroTemplateMacroImpl extends ASTWrapperPsiElement implements CroTemplateMacro {
    public CroTemplateMacroImpl(@NotNull ASTNode node) {
        super(node);
    }
}
