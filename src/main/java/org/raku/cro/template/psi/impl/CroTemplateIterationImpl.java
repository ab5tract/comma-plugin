package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.cro.template.psi.CroTemplateIteration;
import org.raku.cro.template.psi.CroTemplateParameter;
import org.raku.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.raku.cro.template.psi.reference.CroTemplateSymbolKind;
import org.jetbrains.annotations.NotNull;

public class CroTemplateIterationImpl extends ASTWrapperPsiElement implements CroTemplateIteration {
    public CroTemplateIterationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        CroTemplateParameter parameter = PsiTreeUtil.getChildOfType(this, CroTemplateParameter.class);
        if (parameter != null) {
            collector.offer(parameter.getName(), CroTemplateSymbolKind.Variable, parameter);
        }
    }
}
