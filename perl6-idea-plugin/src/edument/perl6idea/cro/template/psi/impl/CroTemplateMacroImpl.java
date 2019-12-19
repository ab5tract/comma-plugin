package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import edument.perl6idea.cro.template.psi.CroTemplateSignature;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.jetbrains.annotations.NotNull;

public class CroTemplateMacroImpl extends ASTWrapperPsiElement implements CroTemplateMacro {
    public CroTemplateMacroImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        CroTemplateSignature signature = PsiTreeUtil.getChildOfType(this, CroTemplateSignature.class);
        if (signature != null)
            signature.offerAllParametersTo(collector);
    }
}
