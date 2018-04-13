package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.Perl6SymbolLike;
import org.jetbrains.annotations.NotNull;

public class Perl6SubsetImpl extends Perl6SymbolLike implements Perl6Subset {
    public Perl6SubsetImpl(@NotNull ASTNode node) {
        super(node);
    }
}
