package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuArrayShape;
import org.jetbrains.annotations.NotNull;

public class RakuArrayShapeImpl extends ASTWrapperPsiElement implements RakuArrayShape {
    public RakuArrayShapeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
