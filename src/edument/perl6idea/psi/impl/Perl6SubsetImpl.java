package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Subset;
import edument.perl6idea.psi.Perl6TypeLike;
import org.jetbrains.annotations.NotNull;

public class Perl6SubsetImpl extends Perl6TypeLike implements Perl6Subset {
    public Perl6SubsetImpl(@NotNull ASTNode node) {
        super(node);
    }
}
