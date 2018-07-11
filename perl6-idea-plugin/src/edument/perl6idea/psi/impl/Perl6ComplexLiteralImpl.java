package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ComplexLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6ComplexLiteralImpl extends ASTWrapperPsiElement implements Perl6ComplexLiteral {
    public Perl6ComplexLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }
}
