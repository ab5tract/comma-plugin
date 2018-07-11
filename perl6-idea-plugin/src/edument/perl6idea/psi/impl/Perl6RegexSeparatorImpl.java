package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexSeparator;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexSeparatorImpl extends ASTWrapperPsiElement implements Perl6RegexSeparator {
    public Perl6RegexSeparatorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
