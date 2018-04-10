package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ExternalElement;
import edument.perl6idea.psi.Perl6ImportStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6ImportStatementImpl extends Perl6ExternalElement implements Perl6ImportStatement {
    public Perl6ImportStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
