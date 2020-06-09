package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6NumLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6NumLiteralImpl extends ASTWrapperPsiElement implements Perl6NumLiteral {
    public Perl6NumLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Num";
    }
}
