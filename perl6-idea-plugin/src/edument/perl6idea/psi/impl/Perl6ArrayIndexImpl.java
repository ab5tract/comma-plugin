package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ArrayIndex;
import org.jetbrains.annotations.NotNull;

public class Perl6ArrayIndexImpl extends ASTWrapperPsiElement implements Perl6ArrayIndex {
    public Perl6ArrayIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
