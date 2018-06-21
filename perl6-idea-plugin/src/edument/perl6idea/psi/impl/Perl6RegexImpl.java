package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Regex;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexImpl extends ASTWrapperPsiElement implements Perl6Regex {
    public Perl6RegexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
