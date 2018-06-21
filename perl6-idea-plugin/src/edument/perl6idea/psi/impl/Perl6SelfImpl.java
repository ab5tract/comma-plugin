package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Self;
import org.jetbrains.annotations.NotNull;

public class Perl6SelfImpl extends ASTWrapperPsiElement implements Perl6Self {
    public Perl6SelfImpl(@NotNull ASTNode node) {
        super(node);
    }
}
