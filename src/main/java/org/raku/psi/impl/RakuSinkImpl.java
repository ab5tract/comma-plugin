package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuSink;
import org.jetbrains.annotations.NotNull;

public class RakuSinkImpl extends ASTWrapperPsiElement implements RakuSink {
    public RakuSinkImpl(@NotNull ASTNode node) {
        super(node);
    }
}
