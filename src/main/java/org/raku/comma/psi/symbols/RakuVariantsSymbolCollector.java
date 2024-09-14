package org.raku.comma.psi.symbols;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.util.containers.ContainerUtil.newHashSet;

public class RakuVariantsSymbolCollector implements RakuSymbolCollector {
    private final String wantedName;
    private final Set<String> traversedNames = new HashSet<>();
    private final Set<RakuSymbolKind> wantedKinds;
    private final Map<String, RakuSymbol> seen = new HashMap<>();
    private final List<RakuSymbol> multi = new LinkedList<>();
    private double myPriority = 1000;

    public RakuVariantsSymbolCollector(String wantedName, RakuSymbolKind... wantedKinds) {
        this.wantedName = wantedName;
        this.wantedKinds = newHashSet(wantedKinds);
    }

    public RakuVariantsSymbolCollector(RakuSymbolKind... wantedKinds) {
        this.wantedName = null;
        this.wantedKinds = newHashSet(wantedKinds);
    }

    @Override
    public boolean shouldTraverse(String packageName) {
        return traversedNames.add(packageName);
    }

    @Override
    public void offerSymbol(RakuSymbol symbol) {
        String name = symbol.getName();
        if (wantedKinds.contains(symbol.getKind()) && (wantedName == null || Objects.equals(wantedName, name)) && !seen.containsKey(name)) {
            symbol.setPriority(myPriority);
            seen.put(name, symbol);
        }
    }

    @Override
    public void offerMultiSymbol(RakuSymbol symbol, boolean isProto) {
        String name = symbol.getName();
        if (wantedKinds.contains(symbol.getKind()) && (wantedName == null || Objects.equals(wantedName, name))) {
            symbol.setPriority(myPriority);
            multi.add(symbol);
        }
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public Collection<RakuSymbol> getVariants() {
        return Stream.concat(seen.values().stream(), multi.stream()).collect(Collectors.toList());
    }

    @Override
    public void decreasePriority() {
        myPriority -= 10;
    }
}
