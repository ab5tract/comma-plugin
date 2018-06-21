package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ColonPair;
import org.jetbrains.annotations.NotNull;

public class Perl6ColonPairImpl extends ASTWrapperPsiElement implements Perl6ColonPair {
    public Perl6ColonPairImpl(@NotNull ASTNode node) {
        super(node);
    }
}
