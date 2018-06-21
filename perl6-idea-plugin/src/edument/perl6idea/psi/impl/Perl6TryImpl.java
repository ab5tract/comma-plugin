package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Try;
import org.jetbrains.annotations.NotNull;

public class Perl6TryImpl extends ASTWrapperPsiElement implements Perl6Try {
    public Perl6TryImpl(@NotNull ASTNode node) {
        super(node);
    }
}
