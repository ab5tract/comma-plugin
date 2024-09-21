package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuZipMetaOp;
import org.jetbrains.annotations.NotNull;

public class RakuZipMetaOpImpl extends RakuASTWrapperPsiElement implements RakuZipMetaOp {
    public RakuZipMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
