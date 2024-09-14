package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuArrayShape;
import org.jetbrains.annotations.NotNull;

public class RakuArrayShapeImpl extends ASTWrapperPsiElement implements RakuArrayShape {
    public RakuArrayShapeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
