package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6OperatorAdverb;
import org.jetbrains.annotations.NotNull;

public class Perl6OperatorAdverbImpl extends ASTWrapperPsiElement implements Perl6OperatorAdverb {
    public Perl6OperatorAdverbImpl(@NotNull ASTNode node) {
        super(node);
    }
}
