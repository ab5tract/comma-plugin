package org.raku.comma.psi.impl;

import org.raku.comma.psi.RakuASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.raku.comma.psi.PodConfiguration;
import org.jetbrains.annotations.NotNull;

public class PodConfigurationImpl extends RakuASTWrapperPsiElement implements PodConfiguration {
    public PodConfigurationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
