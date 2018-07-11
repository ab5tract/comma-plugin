package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6OperatorAdverbApplication;
import org.jetbrains.annotations.NotNull;

public class Perl6OperatorAdverbApplicationImpl extends ASTWrapperPsiElement implements Perl6OperatorAdverbApplication {
    public Perl6OperatorAdverbApplicationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
