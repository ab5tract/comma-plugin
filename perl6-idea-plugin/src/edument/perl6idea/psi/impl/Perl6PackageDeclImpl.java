package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
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
        if (collector.isSatisfied()) return;
        contributeMethods(collector);
        if (collector.isSatisfied()) return;
        if (collector.areInstanceSymbolsRelevant()) {
            contributeFromElders(collector);
        }
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

    private void contributeMethods(Perl6SymbolCollector collector) {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (list == null) return;
        for (PsiElement child : list.getChildren()) {
            if (child.getFirstChild() instanceof Perl6RoutineDecl) {
                ((Perl6RoutineDecl)child.getFirstChild()).contributeSymbols(collector);
                if (collector.isSatisfied()) return;
            }
        }
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        super.contributeSymbols(collector);
        contributeNestedPackagesWithPrefix(collector, getPackageName() + "::");
    }

    private void contributeFromElders(Perl6SymbolCollector collector) {
        Perl6PackageDeclStub stub = getStub();
        List<Pair<String, Perl6PackageDecl>> perl6PackageDecls = new ArrayList<>();
        List<String> externals = new ArrayList<>();
        boolean isAny = true;
        boolean isMu = true;

        if (stub != null) {
            List<StubElement> children = stub.getChildrenStubs();
            for (StubElement child : children) {
                if (!(child instanceof Perl6TraitStub)) continue;
                Perl6TraitStub traitStub = (Perl6TraitStub)child;
                if (!traitStub.getTraitModifier().equals("does") && !traitStub.getTraitModifier().equals("is")) continue;
                for (StubElement maybeType : traitStub.getChildrenStubs())
                    if (maybeType instanceof Perl6TypeNameStub) {
                        Perl6TypeNameStub typeNameStub = (Perl6TypeNameStub)maybeType;
                        Perl6TypeName psi = typeNameStub.getPsi();
                        if (psi == null) continue;
                        PsiReference ref = psi.getReference();
                        if (ref == null) continue;
                        PsiElement decl = ref.resolve();
                        if (decl != null) perl6PackageDecls.add(Pair.create(traitStub.getTraitModifier(), (Perl6PackageDecl)decl));
                        else externals.add(typeNameStub.getTypeName());
                        if ((typeNameStub).getTypeName().equals("Mu"))
                            isAny = false;
                    }
            }
        } else {
            for (Perl6Trait trait : getTraits()) {
                if (trait.getTraitModifier().equals("does") || trait.getTraitModifier().equals("is")) {
                    PsiElement element = trait.getTraitModifier().equals("does") ?
                                         PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class) :
                                         PsiTreeUtil.findChildOfType(trait, Perl6IsTraitName.class);
                    if (element == null) continue;
                    PsiReference ref = element.getReference();
                    if (ref == null) continue;
                    PsiElement decl = ref.resolve();
                    if (decl != null) perl6PackageDecls.add(Pair.create(trait.getTraitModifier(), (Perl6PackageDecl)decl));
                    else externals.add(trait.getTraitName());
                    if (trait.getTraitName().equals("Mu"))
                        isAny = false;
                }
            }
        }

        if (isAny)
            for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Any", this).methods())
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
        if (isMu)
            for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Mu", this).methods())
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));

        for (Pair<String, Perl6PackageDecl> pair : perl6PackageDecls) {
            Perl6PackageDecl typeRef = pair.second;
            String mod = pair.first;
            // We allow gathering of private parts from roles, but not classes
            collector.setAreInternalPartsCollected(mod.equals("does"));

            // Local perl6PackageDecl
            Perl6PackageDeclStub roleStub = typeRef.getStub();
            // Contribute perl6PackageDecl internals
            if (roleStub == null) contributeLocalPackage(collector, typeRef);
            else contributeLocalPackageStub(collector, roleStub);
            if (collector.isSatisfied()) return;
            // Contribute perl6PackageDecl itself
            typeRef.contributeScopeSymbols(collector);
            if (collector.isSatisfied()) return;
        }
        for (String extType : externals) {
            // It can be either external perl6PackageDecl or non-existent one
            // Firstly, chop off possible parametrized roles
            int index = extType.indexOf('[');
            if (index != -1)
                extType = extType.substring(0, index);
            contributeExternalPackage(collector, extType);
            if (collector.isSatisfied()) return;
        }
    }

    private static void contributeLocalPackage(Perl6SymbolCollector collector, PsiElement resolve) {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(resolve, Perl6StatementList.class);
        if (list == null) return;
        Perl6Statement[] states = PsiTreeUtil.getChildrenOfType(list, Perl6Statement.class);
        if (states == null) return;
        for (Perl6Statement s : states) {
            PsiElement firstChild = s.getFirstChild();
            if (firstChild instanceof Perl6ScopedDecl) {
                Perl6ScopedDecl scopedDecl = (Perl6ScopedDecl)firstChild;
                for (PsiElement child : scopedDecl.getChildren()) {
                    if (!(child instanceof Perl6VariableDecl)) continue;
                    Perl6VariableDecl variableDecl = (Perl6VariableDecl)child;
                    if (!variableDecl.getScope().equals("has")) continue;
                    variableDecl.contributeSymbols(collector);
                }
            }
        }
    }

    private static void contributeLocalPackageStub(Perl6SymbolCollector collector, Perl6PackageDeclStub stub) {
        List<StubElement> children = stub.getChildrenStubs();
        for (StubElement child : children) {
            if (child instanceof Perl6RoutineDeclStub) {
                Perl6RoutineDeclStub routine = (Perl6RoutineDeclStub)child;
                if (routine.isPrivate()) {
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
                        if (!declStub.getScope().equals("has")) continue;
                        Perl6VariableDeclImpl.offerVariableSymbols(
                            collector, declStub.getVariableName(), declStub.getPsi()
                        );
                        if (collector.isSatisfied()) return;
                    }
                }
            }
        }
    }

    private void contributeExternalPackage(Perl6SymbolCollector collector, String typeName) {
        Perl6VariantsSymbolCollector extCollector =
                new Perl6VariantsSymbolCollector(Perl6SymbolKind.ExternalPackage);
        applyExternalSymbolCollector(extCollector);
        if (collector.isSatisfied()) return;
        for (Perl6Symbol pack : extCollector.getVariants()) {
            Perl6ExternalPackage externalPackage = (Perl6ExternalPackage)pack;
            if (!(externalPackage.getPackageKind() == Perl6PackageKind.ROLE &&
                  pack.getName().equals(typeName))) continue;
            for (String sym : externalPackage.privateMethods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, sym));
                if (collector.isSatisfied()) return;
            }
            for (String sym : externalPackage.methods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, "." + sym));
                if (collector.isSatisfied()) return;
            }
            for (String var : externalPackage.attributes()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Variable, var));
                if (!collector.isSatisfied()) {
                    if (Perl6Variable.getTwigil(var) == '.') {
                        collector.offerSymbol(new Perl6ExternalSymbol( // Offer self!foo;
                            Perl6SymbolKind.Method, '!' + var.substring(2)));
                        collector.offerSymbol(new Perl6ExternalSymbol( // Offer self.foo;
                            Perl6SymbolKind.Method, '.' + var.substring(2)));
                    }
                }
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
