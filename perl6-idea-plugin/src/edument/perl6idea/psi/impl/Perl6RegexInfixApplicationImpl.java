package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexInfixApplication;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexInfixApplicationImpl extends ASTWrapperPsiElement implements Perl6RegexInfixApplication {
    public Perl6RegexInfixApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
