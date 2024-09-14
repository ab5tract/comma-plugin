package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuParameterDefault;
import org.jetbrains.annotations.NotNull;

public class RakuParameterDefaultImpl extends ASTWrapperPsiElement implements RakuParameterDefault {
    public RakuParameterDefaultImpl(@NotNull ASTNode node) {
        super(node);
    }
}
