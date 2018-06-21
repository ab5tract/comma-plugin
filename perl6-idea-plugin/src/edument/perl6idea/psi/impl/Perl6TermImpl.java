package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Term;
import org.jetbrains.annotations.NotNull;

public class Perl6TermImpl extends ASTWrapperPsiElement implements Perl6Term {
    public Perl6TermImpl(@NotNull ASTNode node) {
        super(node);
    }
}
