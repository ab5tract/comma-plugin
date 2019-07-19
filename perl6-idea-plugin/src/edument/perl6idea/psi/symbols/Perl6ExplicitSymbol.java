package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public class Perl6ExplicitSymbol implements Perl6Symbol {
    private Perl6SymbolKind kind;
    private PsiNamedElement psi;

    public Perl6ExplicitSymbol(Perl6SymbolKind kind, PsiNamedElement psi) {
        this.kind = kind;
        this.psi = psi;
    }

    @Override
    public Perl6SymbolKind getKind() {
        return kind;
    }

    @Override
    public String getName() {
        return psi.getName();
    }

    @Override
    public PsiElement getPsi() {
        return psi;
    }

    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public boolean isImplicitlyDeclared() {
        return false;
    }
}
