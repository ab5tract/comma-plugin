package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Quasi;
import org.jetbrains.annotations.NotNull;

public class Perl6QuasiImpl extends ASTWrapperPsiElement implements Perl6Quasi {
    public Perl6QuasiImpl(@NotNull ASTNode node) {
        super(node);
    }
}
