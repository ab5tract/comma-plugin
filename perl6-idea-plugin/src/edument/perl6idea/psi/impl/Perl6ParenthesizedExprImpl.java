package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ParenthesizedExpr;
import org.jetbrains.annotations.NotNull;

public class Perl6ParenthesizedExprImpl extends ASTWrapperPsiElement implements Perl6ParenthesizedExpr {
    public Perl6ParenthesizedExprImpl(@NotNull ASTNode node) {
        super(node);
    }
}
