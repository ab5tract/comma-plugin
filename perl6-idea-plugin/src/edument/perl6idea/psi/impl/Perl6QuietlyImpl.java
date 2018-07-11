package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Quietly;
import org.jetbrains.annotations.NotNull;

public class Perl6QuietlyImpl extends ASTWrapperPsiElement implements Perl6Quietly {
    public Perl6QuietlyImpl(@NotNull ASTNode node) {
        super(node);
    }
}
