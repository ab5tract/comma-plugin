package edument.perl6idea.psi.symbols;

import edument.perl6idea.psi.Perl6PsiElement;

public interface Perl6SymbolContributor extends Perl6PsiElement {
    void contributeSymbols(Perl6SymbolCollector collector);
}
