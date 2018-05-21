package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiElement;

public class Perl6ExternalSymbol implements Perl6Symbol {
    public Perl6SymbolKind kind;
    public String name;

    public Perl6ExternalSymbol(Perl6SymbolKind kind, String name) {
        this.kind = kind;
        this.name = name;
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
        return null;
    }

    @Override
    public boolean isExternal() {
        return true;
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
