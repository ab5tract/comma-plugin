package edument.perl6idea.psi.symbols;

import edument.perl6idea.psi.Perl6PsiElement;

/**
 * Contributes lexical symbols; since packages are ultimately chained of lexical
 * environment too, this also includes package-scoped things like `class Foo::Bar [ }`.
 * It excludes things that are installed on meta-objects, such as attributes, methods,
 * and private methods.
 */
public interface Perl6LexicalSymbolContributor extends Perl6PsiElement {
    /**
     * Called with a collector to contribute lexically installed symbols.
     * @param collector
     */
    void contributeLexicalSymbols(Perl6SymbolCollector collector);
}
