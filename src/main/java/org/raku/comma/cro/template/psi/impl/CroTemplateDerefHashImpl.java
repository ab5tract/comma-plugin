package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.cro.template.psi.CroTemplateDerefHash;
import org.jetbrains.annotations.NotNull;

public class CroTemplateDerefHashImpl extends ASTWrapperPsiElement implements CroTemplateDerefHash {
    public CroTemplateDerefHashImpl(@NotNull ASTNode node) {
        super(node);
    }
}
