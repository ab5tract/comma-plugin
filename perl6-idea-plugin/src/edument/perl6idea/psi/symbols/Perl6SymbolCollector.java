package edument.perl6idea.psi.symbols;

/* Collects the result of a resolution. */
public interface Perl6SymbolCollector {
    /* Answers if the package should be traversed */
    boolean shouldTraverse(String packageName);

    /* Offers a symbol to the collector, which if may or may not collect */
    void offerSymbol(Perl6Symbol symbol);

    /* Offers a multi symbol to the collector, which it may or may not collect. */
    void offerMultiSymbol(Perl6Symbol symbol, boolean isProto);

    /* Checks if the collector is already satisfied and there is no point continuing
     * to search. */
    boolean isSatisfied();

    void decreasePriority();
}
