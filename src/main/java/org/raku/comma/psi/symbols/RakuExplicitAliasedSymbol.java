package org.raku.comma.psi.symbols;

import com.intellij.psi.PsiNamedElement;

public class RakuExplicitAliasedSymbol extends RakuExplicitSymbol {
    private final String name;

    public RakuExplicitAliasedSymbol(RakuSymbolKind kind, PsiNamedElement psi, String name) {
        super(kind, psi);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
