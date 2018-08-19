package edument.perl6idea.psi.symbols;

public class Perl6SingleResolutionSymbolCollector extends Perl6PackageConstrainedSymbolCollector {
    private Perl6Symbol result = null;
    private String wantedName;
    private Perl6SymbolKind wantedKind;

    public Perl6SingleResolutionSymbolCollector(String wantedName, Perl6SymbolKind wantedKind) {
        this.wantedName = wantedName;
        this.wantedKind = wantedKind;
    }

    @Override
    public void offerSymbol(Perl6Symbol symbol) {
        if (!acceptablyScoped(symbol))
            return;
        if (result == null && symbol.getKind() == wantedKind &&
            symbol.getName() != null && symbol.getName().equals(wantedName))
            result = symbol;
    }

    @Override
    public boolean isSatisfied() {
        return result != null;
    }

    public Perl6Symbol getResult() {
        return result;
    }
}
