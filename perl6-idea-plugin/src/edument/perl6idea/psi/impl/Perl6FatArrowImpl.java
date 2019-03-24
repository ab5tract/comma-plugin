package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6FatArrow;
import org.jetbrains.annotations.NotNull;

public class Perl6FatArrowImpl extends ASTWrapperPsiElement implements Perl6FatArrow {
    public Perl6FatArrowImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String  inferType() {
        return "Pair";
    }

    @Override
    public String getKey() {
        return getFirstChild().getText();
    }

    @Override
    public PsiElement getValue() {
        return getLastChild();
    }
}
