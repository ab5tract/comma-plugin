package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateParameterDefault;
import org.jetbrains.annotations.NotNull;

public class CroTemplateParameterDefaultImpl extends ASTWrapperPsiElement implements CroTemplateParameterDefault {
    public CroTemplateParameterDefaultImpl(@NotNull ASTNode node) {
        super(node);
    }
}
