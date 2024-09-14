package org.raku.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.cro.template.psi.CroTemplateSeparator;
import org.jetbrains.annotations.NotNull;

public class CroTemplateSeparatorImpl extends ASTWrapperPsiElement implements CroTemplateSeparator {
    public CroTemplateSeparatorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
