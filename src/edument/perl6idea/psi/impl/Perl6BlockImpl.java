package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Block;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6StatementList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Perl6BlockImpl extends ASTWrapperPsiElement implements Perl6Block {
    public Perl6BlockImpl(@NotNull ASTNode node) {
        super(node);
    }
}
