package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuTermDefinition;
import org.jetbrains.annotations.NotNull;

public class RakuTermDefinitionImpl extends RakuASTWrapperPsiElement implements RakuTermDefinition {
    public RakuTermDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
