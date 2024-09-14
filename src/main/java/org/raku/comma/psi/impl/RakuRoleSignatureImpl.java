package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRoleSignature;
import org.jetbrains.annotations.NotNull;

public class RakuRoleSignatureImpl extends ASTWrapperPsiElement implements RakuRoleSignature {
    public RakuRoleSignatureImpl(@NotNull ASTNode node) {
        super(node);
    }
}
