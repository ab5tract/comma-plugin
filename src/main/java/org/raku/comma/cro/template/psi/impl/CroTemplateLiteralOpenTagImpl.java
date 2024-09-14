package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.cro.template.psi.CroTemplateLiteralOpenTag;
import org.jetbrains.annotations.NotNull;

public class CroTemplateLiteralOpenTagImpl extends ASTWrapperPsiElement implements CroTemplateLiteralOpenTag {
    public CroTemplateLiteralOpenTagImpl(@NotNull ASTNode node) {
        super(node);
    }
}
