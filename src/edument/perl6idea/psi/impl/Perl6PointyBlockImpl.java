package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6PointyBlock;
import org.jetbrains.annotations.NotNull;

public class Perl6PointyBlockImpl extends ASTWrapperPsiElement implements Perl6PointyBlock {
    public Perl6PointyBlockImpl(@NotNull ASTNode node) {
        super(node);
    }
}