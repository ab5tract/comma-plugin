package edument.perl6idea.psi.symbols;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Perl6VariantsSymbolCollector implements Perl6SymbolCollector {
    private Set<Perl6SymbolKind> wantedKinds;
    private Map<String, Perl6Symbol> seen = new HashMap<>();
    private List<Perl6Symbol> multi = new LinkedList<>();

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
        if (wantedKinds.contains(symbol.getKind()))
            multi.add(symbol);
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public Collection<Perl6Symbol> getVariants() {
        return Stream.concat(seen.values().stream(), multi.stream()).collect(Collectors.toList());
    }
}
