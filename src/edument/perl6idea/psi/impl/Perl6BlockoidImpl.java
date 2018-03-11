package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Blockoid;
import org.jetbrains.annotations.NotNull;

public class Perl6BlockoidImpl extends ASTWrapperPsiElement implements Perl6Blockoid {
    public Perl6BlockoidImpl(@NotNull ASTNode node) {
        super(node);
    }
}
