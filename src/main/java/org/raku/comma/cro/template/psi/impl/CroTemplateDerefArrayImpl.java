package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.cro.template.psi.CroTemplateDerefArray;
import org.jetbrains.annotations.NotNull;

public class CroTemplateDerefArrayImpl extends ASTWrapperPsiElement implements CroTemplateDerefArray {
    public CroTemplateDerefArrayImpl(@NotNull ASTNode node) {
        super(node);
    }
}
