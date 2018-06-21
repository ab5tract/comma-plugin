package edument.perl6idea.psi.symbols;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.Collection;

public class Perl6ExternalPackage implements Perl6Symbol {
    private Perl6PackageKind kind;
    private String name;
    private Collection<String> privateMethods = new ArrayList<>();
    private Collection<String> attributes = new ArrayList<>();
    private Collection<String> methods = new ArrayList<>();

    public Perl6ExternalPackage(String name, Perl6PackageKind kind) {
        this.name = name;
        this.kind = kind;
    }

    @Override
    public Perl6SymbolKind getKind() {
        return Perl6SymbolKind.ExternalPackage;
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

    public Perl6PackageKind getPackageKind() {
        return kind;
    }

    public Collection<String> privateMethods() {
        return privateMethods;
    }

    public Collection<String> attributes() {
        return attributes;
    }

    public Collection<String> methods() {
        return methods;
    }

    public void addPrivateMethod(String line) {
        privateMethods.add(line);
    }


    public void addAttribute(String line) {
        attributes.add(line);
    }

    public void addMethod(String line) {
        methods.add(line);
    }
}
