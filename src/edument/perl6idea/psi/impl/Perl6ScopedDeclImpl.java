package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ScopedDecl;
import org.jetbrains.annotations.NotNull;

public class Perl6ScopedDeclImpl extends ASTWrapperPsiElement implements Perl6ScopedDecl {
    public Perl6ScopedDeclImpl(@NotNull ASTNode node) {
        super(node);
    }
}