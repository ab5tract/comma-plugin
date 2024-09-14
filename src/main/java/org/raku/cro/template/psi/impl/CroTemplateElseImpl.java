package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateElse;
import org.jetbrains.annotations.NotNull;

public class CroTemplateElseImpl extends ASTWrapperPsiElement implements CroTemplateElse {
    public CroTemplateElseImpl(@NotNull ASTNode node) {
        super(node);
    }
}
