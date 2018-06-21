package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6QuotePair;
import org.jetbrains.annotations.NotNull;

public class Perl6QuotePairImpl extends ASTWrapperPsiElement implements Perl6QuotePair {
    public Perl6QuotePairImpl(@NotNull ASTNode node) {
        super(node);
    }
}
