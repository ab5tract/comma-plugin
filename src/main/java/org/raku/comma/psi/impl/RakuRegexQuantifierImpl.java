package org.raku.comma.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class RakuRegexQuantifierImpl extends ASTWrapperPsiElement implements RakuRegexQuantifier {
    public RakuRegexQuantifierImpl(@NotNull ASTNode node) {
        super(node);
    }
}
