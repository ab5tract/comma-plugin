package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6StrLiteralImpl extends ASTWrapperPsiElement implements Perl6StrLiteral {
    public Perl6StrLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Str";
    }
}
