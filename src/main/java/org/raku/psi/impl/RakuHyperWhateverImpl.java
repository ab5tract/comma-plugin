package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuHyperWhatever;
import org.jetbrains.annotations.NotNull;

public class RakuHyperWhateverImpl extends ASTWrapperPsiElement implements RakuHyperWhatever {
    public RakuHyperWhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
