package edument.perl6idea.psi.symbols;

/**
 * Contributes symbols that are stored against the meta-object of a type, such as
 * methods, private methods, and attributes.
 */
public interface Perl6MOPSymbolContributor {
    /**
     * Called with a collector to contribute symbols that are on the meta-object of the
     * element implementing this interface.
     * @param collector The collector to tell symbols to.
     * @param symbolsAllowed What symbols we should contribute.
     */
    void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed);
}
