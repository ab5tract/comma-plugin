package edument.perl6idea.psi.symbols;

/* Collects the result of a resolution. */
public interface Perl6SymbolCollector {
    /* Offers a symbol to the collector, which if may or may not collect */
    void offerSymbol(Perl6Symbol symbol);

    /* Checks if the collector is already satisfied and there is no point continuing
     * to search. */
    boolean isSatisfied();

    /* Called by the scope search when a package boundary has been crossed */
    void increasePackageDepth();

    /* Is used to check if we should offer package symbols */
    boolean areInstanceSymbolsRelevant();

    /* Indicates what package encloses completed element */
    String enclosingPackageKind();
    void setEnclosingPackageKind(String kind);

    /* Indicates what name has package that encloses completed element */
    String enclosingPackageName();
    void setEnclosingPackageName(String name);

    /* Sets flag indicating if internal parts should be offered */
    void setAreInternalPartsCollected(boolean flag);

    /* Returns flag value that indicates if internal parts of package should be offered */
    boolean areInternalPartsCollected();

    /* Is used to check nesting level of inheritance, so we could differentiate
     * directly composed roles from non-direct ones,
     * starts from 0, first package is 1 */
    void setNestingLevel(int level);
    int getNestingLevel();
}
