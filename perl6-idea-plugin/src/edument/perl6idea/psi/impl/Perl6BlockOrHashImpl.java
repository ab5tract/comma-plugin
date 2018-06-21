package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6BlockOrHash;
import edument.perl6idea.psi.Perl6IfStatement;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

public class Perl6BlockOrHashImpl extends ASTWrapperPsiElement implements Perl6BlockOrHash {
    public Perl6BlockOrHashImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$_", this));
        collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "&?BLOCK", this));
    }
}
