package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateInfix;
import org.jetbrains.annotations.NotNull;

public class CroTemplateInfixImpl extends ASTWrapperPsiElement implements CroTemplateInfix {
    public CroTemplateInfixImpl(@NotNull ASTNode node) {
        super(node);
    }
}
