package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuHyperWhatever;
import org.jetbrains.annotations.NotNull;

public class RakuHyperWhateverImpl extends RakuASTWrapperPsiElement implements RakuHyperWhatever {
    public RakuHyperWhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
