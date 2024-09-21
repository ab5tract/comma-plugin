package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuSink;
import org.jetbrains.annotations.NotNull;

public class RakuSinkImpl extends RakuASTWrapperPsiElement implements RakuSink {
    public RakuSinkImpl(@NotNull ASTNode node) {
        super(node);
    }
}
