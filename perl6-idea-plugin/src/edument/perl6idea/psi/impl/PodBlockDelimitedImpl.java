package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.PodBlockDelimited;
import org.jetbrains.annotations.NotNull;

public class PodBlockDelimitedImpl extends ASTWrapperPsiElement implements PodBlockDelimited {
    public PodBlockDelimitedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
