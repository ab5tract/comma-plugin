package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiNamedElement;

public class Perl6ExplicitAliasedSymbol extends Perl6ExplicitSymbol {
    private String name;

    public Perl6ExplicitAliasedSymbol(Perl6SymbolKind kind, PsiNamedElement psi, String name) {
        super(kind, psi);
        this.name = name;
    }

    public Perl6ExplicitAliasedSymbol(Perl6SymbolKind kind, PsiNamedElement psi, String name, Priority priority) {
        super(kind, psi, priority);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
