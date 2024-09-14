package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuWhatever;
import org.jetbrains.annotations.NotNull;

public class RakuWhateverImpl extends ASTWrapperPsiElement implements RakuWhatever {
    public RakuWhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
