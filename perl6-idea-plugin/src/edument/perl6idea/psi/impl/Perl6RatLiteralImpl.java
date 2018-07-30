package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RatLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6RatLiteralImpl extends ASTWrapperPsiElement implements Perl6RatLiteral {
    public Perl6RatLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Rat";
    }
}
