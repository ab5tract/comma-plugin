package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.cro.template.psi.CroTemplateCondition;
import org.jetbrains.annotations.NotNull;

public class CroTemplateConditionImpl extends ASTWrapperPsiElement implements CroTemplateCondition {
    public CroTemplateConditionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
