package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexSigspace;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexSigspaceImpl extends ASTWrapperPsiElement implements Perl6RegexSigspace {
    public Perl6RegexSigspaceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
