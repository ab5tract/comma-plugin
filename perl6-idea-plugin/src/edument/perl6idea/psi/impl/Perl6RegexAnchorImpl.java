package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexAnchor;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexAnchorImpl extends ASTWrapperPsiElement implements Perl6RegexAnchor {
    public Perl6RegexAnchorImpl(@NotNull ASTNode node) {
        super(node);
    }
}
