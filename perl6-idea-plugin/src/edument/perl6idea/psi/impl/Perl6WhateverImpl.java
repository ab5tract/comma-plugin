package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Whatever;
import org.jetbrains.annotations.NotNull;

public class Perl6WhateverImpl extends ASTWrapperPsiElement implements Perl6Whatever {
    public Perl6WhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
