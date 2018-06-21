package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6WithoutStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6WithoutStatementImpl extends ASTWrapperPsiElement implements Perl6WithoutStatement {
    public Perl6WithoutStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
