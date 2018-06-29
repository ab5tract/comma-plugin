package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Perl6PackageDeclImpl extends Perl6TypeStubBasedPsi<Perl6PackageDeclStub> implements Perl6PackageDecl {
    public Perl6PackageDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6PackageDeclImpl(final Perl6PackageDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getPackageKind() {
        Perl6PackageDeclStub stub = getStub();
        if (stub != null)
            return stub.getPackageKind();

        PsiElement declarator = findChildByType(Perl6TokenTypes.PACKAGE_DECLARATOR);
        return declarator == null ? "package" : declarator.getText();
    }

    @Override
    public String getPackageName() {
        return getName();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:PACKAGE_DECLARATION)";
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, "$?PACKAGE"));
        switch (getPackageKind()) {
            case "class":
            case "grammar":
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, "$?CLASS"));
                break;
            case "role":
                collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, "$?ROLE"));
                collector.offerSymbol(new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$?CLASS", this));
                break;
        }
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        super.contributeSymbols(collector);
        contributeNestedPackagesWithPrefix(collector, getPackageName() + "::");
    }

    @Override
    public void contributeNestedPackagesWithPrefix(Perl6SymbolCollector collector, String prefix) {
        // Walk to find immediately nested packages, but not those within them
        // (we make a recursive contribute call on those).
        Perl6PackageDeclStub stub = getStub();
        if (stub != null) {
            Queue<Stub> visit = new LinkedList<>();
            visit.add(stub);
            while (!visit.isEmpty()) {
                Stub current = visit.remove();
                boolean addChildren = false;
                if (current == stub) {
                    addChildren = true;
                }
                else if (current instanceof Perl6PackageDeclStub) {
                    Perl6PackageDeclStub nested = (Perl6PackageDeclStub)current;
                    if (nested.getScope().equals("our")) {
                        String nestedName = nested.getTypeName();
                        if (nestedName != null && !nestedName.isEmpty()) {
                            Perl6PackageDecl psi = nested.getPsi();
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                psi, prefix + nestedName));
                            psi.contributeNestedPackagesWithPrefix(collector, prefix + nestedName + "::");
                        }
                    }
                }
                else {
                    addChildren = true;
                }
                if (addChildren)
                    visit.addAll(current.getChildrenStubs());
            }
        }
        else {
            Queue<Perl6PsiElement> visit = new LinkedList<>();
            visit.add(this);
            while (!visit.isEmpty()) {
                Perl6PsiElement current = visit.remove();
                boolean addChildren = false;
                if (current == this) {
                    addChildren = true;
                }
                else if (current instanceof Perl6PackageDecl) {
                    Perl6PackageDecl nested = (Perl6PackageDecl)current;
                    if (nested.getScope().equals("our")) {
                        String nestedName = nested.getPackageName();
                        if (nestedName != null && !nestedName.isEmpty()) {
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                nested, prefix + nestedName));
                            nested.contributeNestedPackagesWithPrefix(collector, prefix + nestedName + "::");
                        }
                    }
                }
                else {
                    addChildren = true;
                }
                if (addChildren)
                    for (PsiElement e : current.getChildren())
                        if (e instanceof Perl6PsiElement)
                            visit.add((Perl6PsiElement)e);
            }
        }
    }
}
