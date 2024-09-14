package org.raku.psi.symbols;

import com.intellij.codeInsight.PsiEquivalenceUtil;
import org.raku.psi.RakuPackageDecl;

import java.util.*;

public class RakuSingleResolutionSymbolCollector implements RakuSymbolCollector {
    private final Set<String> traversedNames = new HashSet<>();
    private final List<RakuSymbol> results = new ArrayList<>();
    private final String wantedName;
    private final RakuSymbolKind wantedKind;
    private boolean satisfied = false;
    private boolean wasDeferred = false;

    public RakuSingleResolutionSymbolCollector(String wantedName, RakuSymbolKind wantedKind) {
        this.wantedName = wantedName;
        this.wantedKind = wantedKind;
    }

    @Override
    public boolean shouldTraverse(String packageName) {
        return traversedNames.add(packageName);
    }

    @Override
    public void offerSymbol(RakuSymbol symbol) {
        // If already satisfied, then we're done.
        if (satisfied)
            return;

        // Otherwise, see if it matches.
        if (symbol != null &&
                Objects.equals(symbol.getKind(), wantedKind) &&
                Objects.equals(symbol.getName(), wantedName)) {
            if (wantedKind == RakuSymbolKind.TypeOrConstant && symbol.getPsi() instanceof RakuPackageDecl &&
                ((RakuPackageDecl)symbol.getPsi()).isStubbed()) {
                wasDeferred = true;
                return;
            }
            // If we've already got results, then they were multi results. We're now seeing an
            // only result, so we'll drop it and be satisfied.
            if (symbol instanceof RakuExplicitSymbol)
                ((RakuExplicitSymbol)symbol).setDeferrence(wasDeferred);
            if (results.isEmpty())
                results.add(symbol);
            satisfied = true;
        }
    }

    @Override
    public void offerMultiSymbol(RakuSymbol symbol, boolean isProto) {
        // If already satisfied, then we're done.
        if (satisfied)
            return;

        // If we've already got results, then they were multi results. If we now see a
        // proto, then it's probably outer or parent to what we did see, so don't take
        // any more.
        if (isProto && !results.isEmpty()) {
            satisfied = true;
            return;
        }

        // Otherwise, add it if it matches, but don't set "satisfied" so we can collect
        // more.
        if (symbol != null &&
            Objects.equals(symbol.getKind(), wantedKind) &&
            Objects.equals(symbol.getName(), wantedName)) {
            if (symbol.getPsi() != null &&
                results.stream().anyMatch(r -> PsiEquivalenceUtil.areElementsEquivalent(r.getPsi(), symbol.getPsi())))
                return;
            results.add(symbol);
        }
    }

    @Override
    public boolean isSatisfied() {
        return satisfied;
    }

    public RakuSymbol getResult() {
        return results.isEmpty() ? null : results.get(0);
    }

    public List<RakuSymbol> getResults() {
        return results;
    }

    // We do not consider e.g. signature matching when doing a resolution,
    // so priority concept does not work here
    @Override
    public void decreasePriority() {}
}
