package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.RakuRegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class RakuRegexQuantifierImpl extends RakuASTWrapperPsiElement implements RakuRegexQuantifier {
    public RakuRegexQuantifierImpl(@NotNull ASTNode node) {
        super(node);
    }
}
