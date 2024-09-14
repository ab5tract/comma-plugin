package org.raku.comma.psi.symbols;

/* Collects the result of a resolution. */
public interface RakuSymbolCollector {
    /* Answers if the package should be traversed */
    boolean shouldTraverse(String packageName);

    /* Offers a symbol to the collector, which if may or may not collect */
    void offerSymbol(RakuSymbol symbol);

    /* Offers a multi symbol to the collector, which it may or may not collect. */
    void offerMultiSymbol(RakuSymbol symbol, boolean isProto);

    /* Checks if the collector is already satisfied and there is no point continuing
     * to search. */
    boolean isSatisfied();

    void decreasePriority();
}
