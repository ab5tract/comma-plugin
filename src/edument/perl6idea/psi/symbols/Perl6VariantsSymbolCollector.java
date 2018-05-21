package edument.perl6idea.psi.symbols;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Perl6VariantsSymbolCollector implements Perl6SymbolCollector {
    private Perl6SymbolKind wantedKind;
    private Map<String, Perl6Symbol> seen = new HashMap<>();

    public Perl6VariantsSymbolCollector(Perl6SymbolKind wantedKind) {
        this.wantedKind = wantedKind;
    }

    @Override
    public void offerSymbol(Perl6Symbol symbol) {
        String name = symbol.getName();
        if (symbol.getKind() == wantedKind && !seen.containsKey(name))
            seen.put(name, symbol);
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public Collection<Perl6Symbol> getVariants() {
        return seen.values();
    }
}
