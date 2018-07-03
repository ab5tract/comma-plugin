package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
        if (collector.isSatisfied()) return;
        contributePrivateMethods(collector);
        if (collector.isSatisfied()) return;
        if (collector.areInstanceSymbolsRelevant())
            contributeFromRoles(collector);
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

    private void contributePrivateMethods(Perl6SymbolCollector collector) {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (list == null) return;
        for (PsiElement child : list.getChildren()) {
            if (child.getFirstChild() instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl decl = (Perl6RoutineDecl)child.getFirstChild();
                if (decl.isPrivateMethod() && decl.getRoutineKind().equals("method")) {
                    collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, decl, true));
                    if (collector.isSatisfied()) return;
                }
            }
        }
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        super.contributeSymbols(collector);
        contributeNestedPackagesWithPrefix(collector, getPackageName() + "::");
    }

    private void contributeFromRoles(Perl6SymbolCollector collector) {
        Perl6PackageDeclStub stub = getStub();
        List<Perl6TypeName> typeNames = new ArrayList<>();
        if (stub != null) {
            List<StubElement> children = stub.getChildrenStubs();
            for (StubElement child : children) {
                if (!(child instanceof Perl6TraitStub)) continue;
                Perl6TraitStub traitStub = (Perl6TraitStub)child;
                if (!traitStub.getTraitModifier().equals("does")) continue;
                for (StubElement maybeType : traitStub.getChildrenStubs())
                    if (maybeType instanceof Perl6TypeNameStub)
                        typeNames.add(((Perl6TypeNameStub)maybeType).getPsi());
            }
        } else {
            Perl6Trait[] traits = PsiTreeUtil.getChildrenOfType(this, Perl6Trait.class);
            if (traits == null) return;

            List<Perl6TypeName> types =
                    Arrays.stream(traits)
                          .filter(t -> t.getTraitModifier().equals("does"))
                          .map(t -> PsiTreeUtil.findChildOfType(t, Perl6TypeName.class))
                          .collect(Collectors.toList());
            typeNames.addAll(types);
        }
        for (Perl6TypeName typeName : typeNames) {
            if (typeName == null) continue;
            PsiReference typeRef = typeName.getReference();
            if (typeRef == null) continue;
            Perl6PackageDecl role = (Perl6PackageDecl)typeRef.resolve();
            if (role != null) {
                // Local role
                Perl6PackageDeclStub roleStub = role.getStub();
                // Contribute role internals
                if (roleStub == null) contributeLocalRole(collector, role);
                else contributeLocalRoleStub(collector, roleStub);
                if (collector.isSatisfied()) return;
                // Contribute role itself
                role.contributeScopeSymbols(collector);
                if (collector.isSatisfied()) return;
            } else {
                // It can be either external role or non-existent one
                contributeExternalRole(collector, typeName);
                if (collector.isSatisfied()) return;
            }
        }
    }

    private static void contributeLocalRole(Perl6SymbolCollector collector, PsiElement resolve) {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(resolve, Perl6StatementList.class);
        if (list == null) return;
        Perl6Statement[] states = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
        if (states == null) return;
        for (Perl6Statement s : states) {
            PsiElement firstChild = s.getFirstChild();
            if (firstChild instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl routine = (Perl6RoutineDecl)firstChild;
                if ((!routine.isPrivateMethod() && routine.getRoutineKind().equals("method"))) continue;
                collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, routine));
                if (collector.isSatisfied()) return;
            } else if (firstChild instanceof Perl6ScopedDecl) {
                Perl6ScopedDecl scopedDecl = (Perl6ScopedDecl)firstChild;
                for (PsiElement child : scopedDecl.getChildren()) {
                    if (!(child instanceof Perl6VariableDecl)) continue;
                    Perl6VariableDecl variableDecl = (Perl6VariableDecl)child;
                    if (!variableDecl.getScope().equals("has")) continue;
                    Perl6Variable variable = PsiTreeUtil.findChildOfType(child, Perl6Variable.class);
                    if (variable == null) continue;
                    if (variable.getTwigil() == '!') {
                        collector.offerSymbol(new Perl6ExplicitSymbol(
                                Perl6SymbolKind.Variable, variableDecl
                        ));
                        if (collector.isSatisfied()) return;
                    } else if (variable.getTwigil() == '.') {
                        // Add '!' variant to refer for `has $.foo` as `self!foo`.
                        collector.offerSymbol(new Perl6ExplicitAliasedSymbol(
                                Perl6SymbolKind.Variable, variableDecl,
                                variable.getSigil() + "!" + variable.getVariableName().substring(2)
                        ));
                        if (collector.isSatisfied()) return;
                    }
                }
            }
        }
    }

    private static void contributeLocalRoleStub(Perl6SymbolCollector collector, Perl6PackageDeclStub stub) {
        List<StubElement> children = stub.getChildrenStubs();
        for (StubElement child : children) {
            if (child instanceof Perl6RoutineDeclStub) {
                Perl6RoutineDeclStub routine = (Perl6RoutineDeclStub)child;
                if (routine.isPrivate() && routine.getRoutineKind().equals("method")) {
                    collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Routine, routine.getPsi()));
                    if (collector.isSatisfied()) return;
                }
            }
            else if (child instanceof Perl6ScopedDeclStub) {
                Perl6ScopedDeclStub var = (Perl6ScopedDeclStub)child;
                List<StubElement> stubs = var.getChildrenStubs();
                for (StubElement childStub : stubs) {
                    if (childStub instanceof Perl6VariableDeclStub) {
                        Perl6VariableDeclStub declStub = (Perl6VariableDeclStub)childStub;
                        collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, declStub.getPsi()));
                        if (collector.isSatisfied()) return;
                    }
                }
            }
        }
    }

    private void contributeExternalRole(Perl6SymbolCollector collector, Perl6TypeName typeName) {
        Perl6VariantsSymbolCollector extCollector =
                new Perl6VariantsSymbolCollector(Perl6SymbolKind.ExternalPackage);
        applyExternalSymbolCollector(extCollector);
        if (collector.isSatisfied()) return;
        for (Perl6Symbol pack : extCollector.getVariants()) {
            Perl6ExternalPackage externalPackage = (Perl6ExternalPackage)pack;
            if (!(externalPackage.getPackageKind() == Perl6PackageKind.ROLE &&
                  pack.getName().equals(typeName.getTypeName()))) continue;
            for (String sym : externalPackage.privateMethods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Routine, sym));
                if (collector.isSatisfied()) return;
            }
            for (String var : externalPackage.attributes()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Variable, var));
                if (collector.isSatisfied()) return;
            }
        }
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
                            if (collector.isSatisfied()) return;
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
                            if (collector.isSatisfied()) return;
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
