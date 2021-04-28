package edument.perl6idea.psi.symbols;

import com.intellij.util.containers.ContainerUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Perl6VariantsSymbolCollector implements Perl6SymbolCollector {
    private final Set<String> traversedNames = new HashSet<>();
    private final Set<Perl6SymbolKind> wantedKinds;
    private final Map<String, Perl6Symbol> seen = new HashMap<>();
    private final List<Perl6Symbol> multi = new LinkedList<>();
    private double myPriority = 1000;

    public Perl6VariantsSymbolCollector(Perl6SymbolKind... wantedKinds) {
        this.wantedKinds = ContainerUtil.set(wantedKinds);
    }

    @Override
    public boolean shouldTraverse(String packageName) {
        return traversedNames.add(packageName);
    }

    @Override
    public void offerSymbol(Perl6Symbol symbol) {
        String name = symbol.getName();
        if (wantedKinds.contains(symbol.getKind()) && !seen.containsKey(name)) {
            symbol.setPriority(myPriority);
            seen.put(name, symbol);
        }
    }

    @Override
    public void offerMultiSymbol(Perl6Symbol symbol, boolean isProto) {
        if (wantedKinds.contains(symbol.getKind())) {
            symbol.setPriority(myPriority);
            multi.add(symbol);
        }
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public Collection<Perl6Symbol> getVariants() {
        return Stream.concat(seen.values().stream(), multi.stream()).collect(Collectors.toList());
    }

    @Override
    public void decreasePriority() {
        myPriority -= 10;
    }
}
