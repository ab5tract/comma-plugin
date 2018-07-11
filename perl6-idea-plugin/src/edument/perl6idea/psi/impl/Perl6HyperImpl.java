package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Hyper;
import org.jetbrains.annotations.NotNull;

public class Perl6HyperImpl extends ASTWrapperPsiElement implements Perl6Hyper {
    public Perl6HyperImpl(@NotNull ASTNode node) {
        super(node);
    }
}
