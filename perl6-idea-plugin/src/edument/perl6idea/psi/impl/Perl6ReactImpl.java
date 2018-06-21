package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6React;
import org.jetbrains.annotations.NotNull;

public class Perl6ReactImpl extends ASTWrapperPsiElement implements Perl6React {
    public Perl6ReactImpl(@NotNull ASTNode node) {
        super(node);
    }
}
