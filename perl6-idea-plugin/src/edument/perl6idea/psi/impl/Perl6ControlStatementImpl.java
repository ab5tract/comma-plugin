package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ControlStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6ControlStatementImpl extends ASTWrapperPsiElement implements Perl6ControlStatement {
    public Perl6ControlStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
