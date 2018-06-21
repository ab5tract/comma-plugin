package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Do;
import org.jetbrains.annotations.NotNull;

public class Perl6DoImpl extends ASTWrapperPsiElement implements Perl6Do {
    public Perl6DoImpl(@NotNull ASTNode node) {
        super(node);
    }
}
