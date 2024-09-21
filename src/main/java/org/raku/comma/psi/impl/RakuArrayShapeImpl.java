package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuArrayShape;
import org.jetbrains.annotations.NotNull;

public class RakuArrayShapeImpl extends RakuASTWrapperPsiElement implements RakuArrayShape {
    public RakuArrayShapeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
