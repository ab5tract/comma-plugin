package edument.perl6idea.psi.symbols;

import java.util.*;

public class Perl6VariantsSymbolCollector implements Perl6SymbolCollector {
    private Set<Perl6SymbolKind> wantedKinds;
    private Map<String, Perl6Symbol> seen = new HashMap<>();

    public Perl6VariantsSymbolCollector(Perl6SymbolKind... wantedKinds) {
        this.wantedKinds = new HashSet<>(Arrays.asList(wantedKinds));
    }

    @Override
    public void offerSymbol(Perl6Symbol symbol) {
        String name = symbol.getName();
        if (wantedKinds.contains(symbol.getKind()) && !seen.containsKey(name))
            seen.put(name, symbol);
    }

    @Override
    public void offerMultiSymbol(Perl6Symbol symbol, boolean isProto) {
        String name = symbol.getName();
        if (wantedKinds.contains(symbol.getKind()) && !seen.containsKey(name))
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
