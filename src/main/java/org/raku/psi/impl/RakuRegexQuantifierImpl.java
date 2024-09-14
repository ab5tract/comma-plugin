package org.raku.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.psi.RakuRegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class RakuRegexQuantifierImpl extends ASTWrapperPsiElement implements RakuRegexQuantifier {
    public RakuRegexQuantifierImpl(@NotNull ASTNode node) {
        super(node);
    }
}
