package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6HashIndex;
import org.jetbrains.annotations.NotNull;

public class Perl6HashIndexImpl extends ASTWrapperPsiElement implements Perl6HashIndex {
    public Perl6HashIndexImpl(@NotNull ASTNode node) {
        super(node);
    }
}
