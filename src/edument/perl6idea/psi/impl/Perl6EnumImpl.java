package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.Perl6SymbolLike;
import org.jetbrains.annotations.NotNull;

public class Perl6EnumImpl extends Perl6SymbolLike implements Perl6Enum {
    public Perl6EnumImpl(@NotNull ASTNode node) {
        super(node);
    }
}
