package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6LongName;
import org.jetbrains.annotations.NotNull;

public class Perl6LongNameImpl extends ASTWrapperPsiElement implements Perl6LongName {
    public Perl6LongNameImpl(@NotNull ASTNode node) {
        super(node);
    }
}
