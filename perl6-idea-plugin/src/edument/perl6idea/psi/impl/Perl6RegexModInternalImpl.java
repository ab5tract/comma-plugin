package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexModInternal;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexModInternalImpl extends ASTWrapperPsiElement implements Perl6RegexModInternal {
    public Perl6RegexModInternalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
