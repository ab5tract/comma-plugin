package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateElsif;
import org.jetbrains.annotations.NotNull;

public class CroTemplateElsifImpl extends ASTWrapperPsiElement implements CroTemplateElsif {
    public CroTemplateElsifImpl(@NotNull ASTNode node) {
        super(node);
    }
}
