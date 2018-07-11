package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6SubCall;
import org.jetbrains.annotations.NotNull;

public class Perl6SubCallImpl extends ASTWrapperPsiElement implements Perl6SubCall {
    public Perl6SubCallImpl(@NotNull ASTNode node) {
        super(node);
    }
}
