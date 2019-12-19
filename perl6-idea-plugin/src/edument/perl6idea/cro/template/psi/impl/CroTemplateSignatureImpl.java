package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.psi.CroTemplateParameter;
import edument.perl6idea.cro.template.psi.CroTemplateSignature;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CroTemplateSignatureImpl extends ASTWrapperPsiElement implements CroTemplateSignature {
    public CroTemplateSignatureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void offerAllParametersTo(CroTemplateSymbolCollector collector) {
        for (CroTemplateParameter parameter : PsiTreeUtil.findChildrenOfType(this, CroTemplateParameter.class)) {
            collector.offer(parameter.getName(), parameter);
        }
    }
}
