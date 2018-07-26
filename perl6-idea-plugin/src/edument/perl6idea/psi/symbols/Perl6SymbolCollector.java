package edument.perl6idea.psi.symbols;

/* Collects the result of a resolution. */
public interface Perl6SymbolCollector {
    /* Offers a symbol to the collector, which if may or may not collect */
    void offerSymbol(Perl6Symbol symbol);

    /* Checks if the collector is already satisfied and there is no point continuing
     * to search. */
    boolean isSatisfied();

    /* Called by the scope search when a package boundary has been crossed. */
    void increasePackageDepth();

    /* Is used to check if we should offer package symbols */
    boolean areInstanceSymbolsRelevant();

    /* Sets flag indicating if internal parts should be offered */
    void setAreInternalPartsCollected(boolean flag);

    /* Returns flag value that indicates if internal parts of package should be offered */
    boolean areInternalPartsCollected();
}
