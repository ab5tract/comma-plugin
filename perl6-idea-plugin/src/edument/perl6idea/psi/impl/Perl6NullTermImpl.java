package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6NullTerm;
import org.jetbrains.annotations.NotNull;

public class Perl6NullTermImpl extends ASTWrapperPsiElement implements Perl6NullTerm {
    public Perl6NullTermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
