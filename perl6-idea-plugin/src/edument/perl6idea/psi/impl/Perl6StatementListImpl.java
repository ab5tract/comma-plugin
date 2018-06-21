package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

public class Perl6StatementListImpl extends ASTWrapperPsiElement implements Perl6StatementList {
    public Perl6StatementListImpl(@NotNull ASTNode node) {
        super(node);
    }
}
