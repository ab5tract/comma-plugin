package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Start;
import org.jetbrains.annotations.NotNull;

public class Perl6StartImpl extends ASTWrapperPsiElement implements Perl6Start {
    public Perl6StartImpl(@NotNull ASTNode node) {
        super(node);
    }
}
