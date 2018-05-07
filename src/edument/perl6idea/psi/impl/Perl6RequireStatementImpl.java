package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ExternalElement;
import edument.perl6idea.psi.Perl6RequireStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6RequireStatementImpl extends Perl6ExternalElement implements Perl6RequireStatement {
    public Perl6RequireStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
