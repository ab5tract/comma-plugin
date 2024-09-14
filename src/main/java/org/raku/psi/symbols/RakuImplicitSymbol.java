package org.raku.psi.symbols;

import com.intellij.psi.PsiElement;

public class RakuImplicitSymbol implements RakuSymbol {
    public final RakuSymbolKind kind;
    public final String name;
    public PsiElement resolvesTo;

    public RakuImplicitSymbol(RakuSymbolKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public RakuImplicitSymbol(RakuSymbolKind kind, String name, PsiElement resolvesTo) {
        this.kind = kind;
        this.name = name;
        this.resolvesTo = resolvesTo;
    }

    @Override
    public RakuSymbolKind getKind() {
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
