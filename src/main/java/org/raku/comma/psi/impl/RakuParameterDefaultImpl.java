package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuParameterDefault;
import org.jetbrains.annotations.NotNull;

public class RakuParameterDefaultImpl extends RakuASTWrapperPsiElement implements RakuParameterDefault {
    public RakuParameterDefaultImpl(@NotNull ASTNode node) {
        super(node);
    }
}
