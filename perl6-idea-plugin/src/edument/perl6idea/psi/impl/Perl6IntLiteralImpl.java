package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6IntLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6IntLiteralImpl extends ASTWrapperPsiElement implements Perl6IntLiteral {
    public Perl6IntLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Int";
    }
}
