package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhatever;
import org.jetbrains.annotations.NotNull;

public class RakuWhateverImpl extends RakuASTWrapperPsiElement implements RakuWhatever {
    public RakuWhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
