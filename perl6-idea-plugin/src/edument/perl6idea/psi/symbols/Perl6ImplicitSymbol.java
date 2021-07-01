package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiElement;

public class Perl6ImplicitSymbol implements Perl6Symbol {
    public final Perl6SymbolKind kind;
    public final String name;
    public PsiElement resolvesTo;

    public Perl6ImplicitSymbol(Perl6SymbolKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public Perl6ImplicitSymbol(Perl6SymbolKind kind, String name, PsiElement resolvesTo) {
        this.kind = kind;
        this.name = name;
        this.resolvesTo = resolvesTo;
    }

    @Override
    public Perl6SymbolKind getKind() {
        return kind;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PsiElement getPsi() {
        return resolvesTo;
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
        return true;
    }

    @Override
    public void setPriority(double priority) {}

    @Override
    public double getPriority() {
        return 1000;
    }

    @Override
    public boolean wasDeferred() {
        return false;
    }
}
