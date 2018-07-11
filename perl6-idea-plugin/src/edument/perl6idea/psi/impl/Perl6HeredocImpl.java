package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Heredoc;
import org.jetbrains.annotations.NotNull;

public class Perl6HeredocImpl extends ASTWrapperPsiElement implements Perl6Heredoc {
    public Perl6HeredocImpl(@NotNull ASTNode node) {
        super(node);
    }
}
