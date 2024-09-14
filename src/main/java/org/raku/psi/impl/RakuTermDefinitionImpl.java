package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuTermDefinition;
import org.jetbrains.annotations.NotNull;

public class RakuTermDefinitionImpl extends ASTWrapperPsiElement implements RakuTermDefinition {
    public RakuTermDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
