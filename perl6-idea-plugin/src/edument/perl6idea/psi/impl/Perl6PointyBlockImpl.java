package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6PointyBlock;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class Perl6PointyBlockImpl extends ASTWrapperPsiElement implements Perl6PointyBlock {
    public Perl6PointyBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Perl6Parameter[] getParams() {
        Perl6Signature sig = findChildByClass(Perl6Signature.class);
        if (sig != null) return sig.getParameters();
        return new Perl6Parameter[]{};
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&?BLOCK", this));
    }
}
