package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Supply;
import org.jetbrains.annotations.NotNull;

public class Perl6SupplyImpl extends ASTWrapperPsiElement implements Perl6Supply {
    public Perl6SupplyImpl(@NotNull ASTNode node) {
        super(node);
    }
}
