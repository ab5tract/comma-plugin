package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Once;
import org.jetbrains.annotations.NotNull;

public class Perl6OnceImpl extends ASTWrapperPsiElement implements Perl6Once {
    public Perl6OnceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
