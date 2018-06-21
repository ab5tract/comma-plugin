package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6HyperWhatever;
import org.jetbrains.annotations.NotNull;

public class Perl6HyperWhateverImpl extends ASTWrapperPsiElement implements Perl6HyperWhatever {
    public Perl6HyperWhateverImpl(@NotNull ASTNode node) {
        super(node);
    }
}
