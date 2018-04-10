package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.Perl6TypeLike;
import org.jetbrains.annotations.NotNull;

public class Perl6EnumImpl extends Perl6TypeLike implements Perl6Enum {
    public Perl6EnumImpl(@NotNull ASTNode node) {
        super(node);
    }
}
