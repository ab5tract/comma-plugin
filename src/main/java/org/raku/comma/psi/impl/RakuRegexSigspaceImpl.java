package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexSigspace;
import org.jetbrains.annotations.NotNull;

public class RakuRegexSigspaceImpl extends RakuASTWrapperPsiElement implements RakuRegexSigspace {
    public RakuRegexSigspaceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
