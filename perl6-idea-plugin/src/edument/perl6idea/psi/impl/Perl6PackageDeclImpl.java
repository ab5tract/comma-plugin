package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Perl6PackageDeclImpl extends Perl6TypeStubBasedPsi<Perl6PackageDeclStub> implements Perl6PackageDecl, PsiMetaOwner {
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
        if (collector.enclosingPackageKind() == null) {
            collector.setEnclosingPackageKind(getPackageKind());
        }
        if (collector.enclosingPackageName() == null) {
            collector.setEnclosingPackageName(getPackageName());
        }
        collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, "$?PACKAGE"));
        if (collector.isSatisfied()) return;
        List<String> trusts = getTrusts();
        // If it is not first encountered package (enclosing one) and current package trusts
        boolean isTrusted = !getPackageName().equals(collector.enclosingPackageName()) &&
                            trusts.contains(collector.enclosingPackageName());
        contributeInternals(collector, isTrusted);
        if (collector.isSatisfied()) return;
        if (collector.areInstanceSymbolsRelevant()) {
            contributeFromElders(collector);
        }
        if (collector.isSatisfied()) return;
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

    private List<String> getTrusts() {
        List<String> trusts = new ArrayList<>();
        Perl6StatementList statementList = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (statementList == null) return new ArrayList<>();
        for (PsiElement statement : statementList.getChildren()) {
            if (statement.getFirstChild() instanceof Perl6Trusts)
                trusts.add(((Perl6Trusts)statement.getFirstChild()).getTypeName());
        }
        return trusts;
    }

    private void contributeInternals(Perl6SymbolCollector collector, boolean isTrusted) {
        Perl6PackageDeclStub stub = getStub();
        if (stub != null) {
            for (StubElement nestedStub : stub.getChildrenStubs()) {
                if (nestedStub instanceof Perl6RoutineDeclStub) {
                    Perl6RoutineDeclStub declStub = (Perl6RoutineDeclStub)nestedStub;
                    if (isTrusted && declStub.isPrivate()) {
                        boolean areInternalsCollected = collector.areInternalPartsCollected();
                        collector.setAreInternalPartsCollected(true);
                        Perl6RoutineDeclImpl.offerRoutineSymbols(
                            collector,
                            "!" + getPackageName() + "::" + declStub.getRoutineName().substring(1),
                            declStub.getPsi());
                        collector.setAreInternalPartsCollected(areInternalsCollected);
                        if (collector.isSatisfied()) return;
                    } else {
                        Perl6RoutineDeclImpl.offerRoutineSymbols(
                            collector, declStub.getRoutineName(), declStub.getPsi());
                    }
                    if (collector.isSatisfied()) return;
                } else if (nestedStub instanceof Perl6ScopedDeclStub) {
                    Perl6ScopedDeclStub scopedVar = (Perl6ScopedDeclStub)nestedStub;
                    List<StubElement> stubsUnderScoped = scopedVar.getChildrenStubs();
                    for (StubElement var : stubsUnderScoped) {
                        if (var instanceof Perl6VariableDeclStub) {
                            Perl6VariableDeclStub declStub = (Perl6VariableDeclStub)var;
                            if (!declStub.getScope().equals("has")) continue;
                            Perl6VariableDeclImpl.offerVariableSymbols(
                                collector, declStub.getVariableName(), declStub.getPsi()
                            );
                            if (collector.isSatisfied()) return;
                        }
                    }
                } else if (nestedStub instanceof Perl6RegexDeclStub) {
                    Perl6RegexDeclStub declStub = (Perl6RegexDeclStub)nestedStub;
                    collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Regex, declStub.getPsi(), declStub.getRegexName()));
                    if (collector.isSatisfied()) return;
                }
            }
            return;
        }

        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (list == null) return;
        for (PsiElement child : list.getChildren()) {
            if (child.getFirstChild() instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl decl = (Perl6RoutineDecl)child.getFirstChild();
                if (isTrusted && decl.isPrivate()) {
                    boolean areInternalsCollected = collector.areInternalPartsCollected();
                    collector.setAreInternalPartsCollected(true);
                    Perl6RoutineDeclImpl.offerRoutineSymbols(
                        collector,
                        "!" + getPackageName() + "::" + decl.getRoutineName().substring(1),
                        decl);
                    collector.setAreInternalPartsCollected(areInternalsCollected);
                    if (collector.isSatisfied()) return;
                } else {
                    ((Perl6RoutineDecl)child.getFirstChild()).contributeSymbols(collector);
                }
                if (collector.isSatisfied()) return;
            } else if (child.getFirstChild() instanceof Perl6ScopedDecl) {
                Perl6ScopedDecl decl = (Perl6ScopedDecl)child.getFirstChild();
                if (decl.getScope().equals("has") || decl.getScope().equals("our")) {
                    Perl6VariableDecl varDecl = PsiTreeUtil.getChildOfType(decl, Perl6VariableDecl.class);
                    if (varDecl != null)
                        varDecl.contributeSymbols(collector);
                }
                if (collector.isSatisfied()) return;
            } else if (child.getFirstChild() instanceof Perl6RegexDecl) {
                ((Perl6RegexDecl)child.getFirstChild()).contributeSymbols(collector);
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
        collector.setNestingLevel(collector.getNestingLevel() + 1);
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
                for (StubElement maybeType : traitStub.getChildrenStubs()) {
                    // Check only type names
                    if (!(maybeType instanceof Perl6TypeNameStub)) continue;
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
                if (!(trait.getTraitModifier().equals("does") || trait.getTraitModifier().equals("is"))) continue;
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

        if (isAny)
            for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Any", this).methods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
                if (collector.isSatisfied()) return;
            }
        if (isMu)
            for (String method : Perl6SdkType.getInstance().getCoreSettingSymbol("Mu", this).methods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, '.' + method));
                if (collector.isSatisfied()) return;
            }

        int level = collector.getNestingLevel();
        for (Pair<String, Perl6PackageDecl> pair : perl6PackageDecls) {
            // Local perl6PackageDecl
            Perl6PackageDecl typeRef = pair.second;
            String mod = pair.first;
            // We allow gathering of private parts from roles, not classes
            if (mod.equals("is"))
                collector.setAreInternalPartsCollected(false);
            // Contribute perl6PackageDecl internals using stub or node
            typeRef.contributeScopeSymbols(collector);
            if (collector.isSatisfied()) return;
            collector.setNestingLevel(level);
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

    private void contributeExternalPackage(Perl6SymbolCollector collector, String typeName) {
        Perl6VariantsSymbolCollector extCollector =
                new Perl6VariantsSymbolCollector(Perl6SymbolKind.ExternalPackage);
        applyExternalSymbolCollector(extCollector);
        for (Perl6Symbol pack : extCollector.getVariants()) {
            Perl6ExternalPackage externalPackage = (Perl6ExternalPackage)pack;
            if (!(pack.getName().equals(typeName))) continue;
            if (((Perl6ExternalPackage)pack).getPackageKind() == Perl6PackageKind.ROLE) {
                for (String sym : externalPackage.privateMethods()) {
                    collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, sym));
                    if (collector.isSatisfied()) return;
                }
            }
            for (String sym : externalPackage.methods()) {
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Method, "." + sym));
                if (collector.isSatisfied()) return;
            }
            for (String var : externalPackage.attributes()) {
                if (((Perl6ExternalPackage)pack).getPackageKind() == Perl6PackageKind.CLASS &&
                    Perl6Variable.getTwigil(var) == '!') continue;
                collector.offerSymbol(new Perl6ExternalSymbol(Perl6SymbolKind.Variable, var));
                if (collector.isSatisfied()) return;
                if (Perl6Variable.getTwigil(var) == '.')
                    collector.offerSymbol(new Perl6ExternalSymbol( // Offer self.foo;
                                          Perl6SymbolKind.Method, '.' + var.substring(2)));
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
            contributeNestedPackagesWithPrefixStub(collector, prefix, stub);
        }
        else {
            contributeNestedPackagesWithPrefixNonStub(collector, prefix);
        }
    }

    private void contributeNestedPackagesWithPrefixNonStub(Perl6SymbolCollector collector, String prefix) {
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

    private static void contributeNestedPackagesWithPrefixStub(Perl6SymbolCollector collector, String prefix, Perl6PackageDeclStub stub) {
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

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        PsiElement decl = this;
        String shortName = getPackageName();
        int lastIndexOf = shortName.lastIndexOf(':');
        if (lastIndexOf != -1) {
            shortName = shortName.substring(lastIndexOf + 1);
        }
        String finalShortPackageName = shortName;
        return new PsiMetaData() {
            @Override
            public PsiElement getDeclaration() {
                return decl;
            }

            @Override
            public String getName(PsiElement context) {
                return finalShortPackageName;
            }

            @Override
            public String getName() {
                return finalShortPackageName;
            }

            @Override
            public void init(PsiElement element) {
            }

            @NotNull
            @Override
            public Object[] getDependences() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }
}
