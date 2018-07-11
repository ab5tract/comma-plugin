package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexGroup;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexGroupImpl extends ASTWrapperPsiElement implements Perl6RegexGroup {
    public Perl6RegexGroupImpl(@NotNull ASTNode node) {
        super(node);
    }
}
