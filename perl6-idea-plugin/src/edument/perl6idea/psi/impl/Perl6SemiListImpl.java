package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6SemiList;
import org.jetbrains.annotations.NotNull;

public class Perl6SemiListImpl extends ASTWrapperPsiElement implements Perl6SemiList {
    public Perl6SemiListImpl(@NotNull ASTNode node) {
        super(node);
    }
}
