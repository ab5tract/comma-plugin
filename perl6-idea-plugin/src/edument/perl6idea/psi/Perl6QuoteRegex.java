package edument.perl6idea.psi;

import edument.perl6idea.psi.symbols.Perl6SymbolCollector;

public interface Perl6QuoteRegex extends Perl6PsiElement {
    void contributeRegexVariables(Perl6SymbolCollector collector);
}
