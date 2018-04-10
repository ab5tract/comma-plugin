package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ExternalElement;
import edument.perl6idea.psi.Perl6UseStatement;
import org.jetbrains.annotations.NotNull;

public class Perl6UseStatementImpl extends Perl6ExternalElement implements Perl6UseStatement {
    public Perl6UseStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
