package org.raku.comma.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.cro.template.psi.CroTemplateTopicAccess;
import org.jetbrains.annotations.NotNull;

public class CroTemplateTopicAccessImpl extends ASTWrapperPsiElement implements CroTemplateTopicAccess {
    public CroTemplateTopicAccessImpl(@NotNull ASTNode node) {
        super(node);
    }
}
