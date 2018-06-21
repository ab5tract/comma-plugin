package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Call;
import org.jetbrains.annotations.NotNull;

public class Perl6CallImpl extends ASTWrapperPsiElement implements Perl6Call {
    public Perl6CallImpl(@NotNull ASTNode node) {
        super(node);
    }
}
