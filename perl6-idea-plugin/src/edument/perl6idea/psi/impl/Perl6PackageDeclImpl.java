package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.*;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.stub.index.Perl6LexicalTypeStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Perl6PackageDeclImpl extends Perl6TypeStubBasedPsi<Perl6PackageDeclStub>
        implements Perl6PackageDecl, PsiMetaOwner {
    private Boolean cachedTrustsOthers;

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

        PsiElement declarator = getDeclarator();
        return declarator == null ? "package" : declarator.getText();
    }

    @Override
    public String getPackageName() {
        return getName();
    }

    @Override
    public boolean isStubbed() {
        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (list == null) return false;
        for (PsiElement child : list.getChildren()) {
            if (child.getFirstChild() instanceof Perl6StubCode)
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public PsiElement getPackageKeywordNode() {
        return getDeclarator();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:PACKAGE_DECLARATION)";
    }

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        String packageName = getPackageName();
        if (packageName == null) return;
        collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Variable, this, "$?PACKAGE"));
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

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        super.contributeLexicalSymbols(collector);
        contributeNestedPackagesWithPrefix(collector, getPackageName() + "::");
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        contributeInternals(collector, symbolsAllowed);
        if (collector.isSatisfied())
            return;
        collector.decreasePriority();
        String packageName = getPackageName();
        if (packageName != null && !collector.shouldTraverse(packageName))
            return;
        contributeFromElders(collector, symbolsAllowed);
    }

    // TODO Re-instate trusts support somehow
    private List<String> getTrusts() {
        List<String> trusts = new ArrayList<>();
        Perl6PackageDeclStub stub = getStub();
        if (stub != null) {
            stub.getChildrenStubs().stream()
                    .filter(s -> s instanceof Perl6TypeNameStub)
                    .forEach(s -> trusts.add(((Perl6TypeNameStub)s).getTypeName()));
        } else {
            Perl6StatementList statementList = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
            if (statementList == null) return new ArrayList<>();
            for (PsiElement statement : statementList.getChildren()) {
                if (statement.getFirstChild() instanceof Perl6Trusts)
                    trusts.add(((Perl6Trusts) statement.getFirstChild()).getTypeName());
            }
        }
        return trusts;
    }

    @Override
    public boolean trustsOthers() {
        if (cachedTrustsOthers == null)
            cachedTrustsOthers = !getTrusts().isEmpty();
        return cachedTrustsOthers;
    }

    @Override
    public void subtreeChanged() {
        cachedTrustsOthers = null;
    }

    private void contributeInternals(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        Perl6PackageDeclStub stub = getStub();
        if (stub != null) {
            for (StubElement nestedStub : stub.getChildrenStubs()) {
                if (nestedStub instanceof Perl6RoutineDeclStub) {
                    Perl6RoutineDeclStub declStub = (Perl6RoutineDeclStub)nestedStub;
                    if (declStub.isPrivate() && !symbolsAllowed.privateMethodsVisible)
                        continue;
                    if (declStub.getRoutineKind().equals("submethod") && !symbolsAllowed.submethodsVisible)
                        continue;
                    declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                    if (collector.isSatisfied()) return;
                }
                else if (nestedStub instanceof Perl6ScopedDeclStub) {
                    Perl6ScopedDeclStub scopedVar = (Perl6ScopedDeclStub)nestedStub;
                    List<StubElement> stubsUnderScoped = scopedVar.getChildrenStubs();
                    for (StubElement var : stubsUnderScoped) {
                        if (var instanceof Perl6VariableDeclStub) {
                            Perl6VariableDeclStub declStub = (Perl6VariableDeclStub)var;
                            if (!declStub.getScope().equals("has"))
                                continue;
                            declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                            if (collector.isSatisfied()) return;
                        }
                    }
                } else if (nestedStub instanceof Perl6RegexDeclStub) {
                    Perl6RegexDeclStub declStub = (Perl6RegexDeclStub)nestedStub;
                    declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                    if (collector.isSatisfied()) return;
                }
            }
            return;
        }

        Perl6StatementList list = PsiTreeUtil.findChildOfType(this, Perl6StatementList.class);
        if (list == null) return;
        for (PsiElement child : list.getChildren()) {
            PsiElement firstChild = child.getFirstChild();
            if (firstChild instanceof Perl6RoutineDecl) {
                Perl6RoutineDecl decl = (Perl6RoutineDecl)firstChild;
                decl.contributeMOPSymbols(collector, symbolsAllowed);
            }
            else if (firstChild instanceof Perl6MultiDecl) {
                Perl6RoutineDecl maybeDecl = PsiTreeUtil.getChildOfType(firstChild, Perl6RoutineDecl.class);
                if (maybeDecl != null)
                    maybeDecl.contributeMOPSymbols(collector, symbolsAllowed);
            }
            else if (firstChild instanceof Perl6ScopedDecl) {
                Perl6ScopedDecl decl = (Perl6ScopedDecl)firstChild;
                if (decl.getScope().equals("has")) {
                    Perl6VariableDecl varDecl = PsiTreeUtil.getChildOfType(decl, Perl6VariableDecl.class);
                    if (varDecl != null)
                        varDecl.contributeMOPSymbols(collector, symbolsAllowed);
                }
            }
            else if (firstChild instanceof Perl6RegexDecl) {
                ((Perl6RegexDecl)firstChild).contributeMOPSymbols(collector, symbolsAllowed);
            }
            if (collector.isSatisfied()) return;
        }
    }

    private void contributeFromElders(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        Perl6PackageDeclStub stub = getStub();
        List<Pair<String, Perl6PackageDecl>> perl6PackageDecls = new ArrayList<>();
        List<Pair<String, String>> externals = new ArrayList<>();
        boolean isAny = true;
        boolean isMu = true;
        boolean isGrammar = getPackageKind().equals("grammar");

        if (stub != null) {
            List<StubElement> children = stub.getChildrenStubs();
            for (StubElement child : children) {
                if (!(child instanceof Perl6TraitStub)) continue;
                Perl6TraitStub traitStub = (Perl6TraitStub)child;
                if (!traitStub.getTraitModifier().equals("does") && !traitStub.getTraitModifier().equals("is")) continue;
                String name = traitStub.getTraitName();
                Project project = getProject();
                List<Perl6IndexableType> indexables = new ArrayList<>();
                indexables.addAll(Perl6LexicalTypeStubIndex.getInstance().get(name, project,
                        GlobalSearchScope.projectScope(project)));
                indexables.addAll(Perl6GlobalTypeStubIndex.getInstance().get(name, project,
                        GlobalSearchScope.projectScope(project)));
                if (indexables.size() == 1) {
                    Perl6PackageDecl decl = (Perl6PackageDecl) indexables.get(0);
                    perl6PackageDecls.add(Pair.create(traitStub.getTraitModifier(), decl));
                } else {
                    externals.add(Pair.create(traitStub.getTraitModifier(), name));
                }
                isAny = !name.equals("Mu");
            }
        }
        else {
            for (Perl6Trait trait : getTraits()) {
                if (!(trait.getTraitModifier().equals("does") || trait.getTraitModifier().equals("is"))) continue;
                PsiElement element = trait.getTraitModifier().equals("does") ?
                                     PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class) :
                                     PsiTreeUtil.findChildOfType(trait, Perl6IsTraitName.class);
                if (element == null) continue;
                PsiReference ref = element.getReference();
                if (ref == null) continue;
                PsiElement decl = ref.resolve();
                if (decl instanceof Perl6PackageDecl)
                    perl6PackageDecls.add(Pair.create(trait.getTraitModifier(), (Perl6PackageDecl)decl));
                else
                    externals.add(Pair.create(trait.getTraitModifier(), trait.getTraitName()));
                if (trait.getTraitName().equals("Mu"))
                    isAny = false;
            }
        }

        // Contribute from explicit parents, either local or external
        for (Pair<String, Perl6PackageDecl> pair : perl6PackageDecls) {
            // Local perl6PackageDecl
            Perl6PackageDecl typeRef = pair.second;
            String mod = pair.first;
            boolean isDoes = mod.equals("does");
            typeRef.contributeMOPSymbols(collector, isDoes ? symbolsAllowed.does() : symbolsAllowed.is());
            if (collector.isSatisfied()) return;
            typeRef.contributeScopeSymbols(collector);
            if (collector.isSatisfied()) return;
        }
        for (Pair<String, String> pair : externals) {
            // It can be either external perl6PackageDecl or non-existent one
            boolean isDoes = pair.first.equals("does");
            String extType = pair.second;
            // Chop off possible parametrized roles
            int index = extType.indexOf('[');
            if (index != -1)
                extType = extType.substring(0, index);
            contributeExternalPackage(collector, extType, isDoes ? symbolsAllowed.does() : symbolsAllowed.is());
            if (collector.isSatisfied()) return;
        }

        // Contribute implicit symbols from Any/Mu and Cursor for grammars
        Perl6File coreSetting = Perl6SdkType.getInstance().getCoreSettingFile(getProject());
        MOPSymbolsAllowed allowed = new MOPSymbolsAllowed(false, false, false, getPackageKind().equals("role"));

        if (perl6PackageDecls.size() != 0 || externals.size() != 0)
            return;

        collector.decreasePriority();
        if (isGrammar)
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Cursor", allowed);
        collector.decreasePriority();
        if (isAny)
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Any", allowed);
        collector.decreasePriority();
        if (isMu)
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Mu", allowed);
    }

    private void contributeExternalPackage(Perl6SymbolCollector collector, String typeName,
                                           MOPSymbolsAllowed symbolsAllowed) {
        Perl6SingleResolutionSymbolCollector extCollector =
                new Perl6SingleResolutionSymbolCollector(typeName, Perl6SymbolKind.TypeOrConstant);
        applyExternalSymbolCollector(extCollector);
        Perl6Symbol collectorResult = extCollector.getResult();
        if (collectorResult != null && collectorResult.getPsi() instanceof Perl6PackageDecl) {
            Perl6PackageDecl externalPackage = (Perl6PackageDecl)collectorResult.getPsi();
            externalPackage.contributeMOPSymbols(collector, symbolsAllowed);
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

    @Override
    public List<Perl6PackageDecl> collectParents() {
        List<Perl6PackageDecl> parents = new ArrayList<>();
        for (Perl6Trait trait : getTraits()) {
            Perl6IsTraitName isTrait = PsiTreeUtil.findChildOfType(trait, Perl6IsTraitName.class);
            if (isTrait != null) {
                PsiElement resolved = isTrait.getReference().resolve();
                if (resolved instanceof Perl6PackageDecl)
                    parents.add((Perl6PackageDecl)resolved);
            }
            else {
                Perl6TypeName doesTrait = PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class);
                if (doesTrait != null) {
                    PsiElement resolved = doesTrait.getReference().resolve();
                    if (resolved instanceof Perl6PackageDecl)
                        parents.add((Perl6PackageDecl)resolved);
                }
            }
        }
        return parents;
    }

    @Override
    public List<Perl6PackageDecl> collectChildren() {
        List<Perl6PackageDecl> children = new ArrayList<>();
        Perl6GlobalTypeStubIndex instance = Perl6GlobalTypeStubIndex.getInstance();
        Project project = getProject();
        String name = getPackageName();
        Collection<String> keys = instance.getAllKeys(project);
        for (String key : keys) {
            Collection<Perl6IndexableType> psi = instance.get(key, project, GlobalSearchScope.allScope(project));
            if (psi.size() == 1) {
                for (Perl6IndexableType type : psi) {
                    if (!(type instanceof Perl6PackageDecl))
                        continue;
                    Perl6Trait childTrait = ((Perl6PackageDecl)type).findTrait("does", name);
                    if (childTrait != null)
                        children.add((Perl6PackageDecl)type);
                    childTrait = ((Perl6PackageDecl)type).findTrait("is", name);
                    if (childTrait != null)
                        children.add((Perl6PackageDecl)type);
                }
            }
        }
        return children;
    }

    @Nullable
    @Override
    public PsiMetaData getMetaData() {
        PsiElement decl = this;
        String shortName = getPackageName();
        if (shortName == null)
            shortName = "";
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
            public Object[] getDependencies() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        PsiElement nameElement = Perl6ElementFactory.createTypeDeclarationName(getProject(), name);
        PsiElement identifier = getNameIdentifier();
        if (identifier != null)
            identifier.replace(nameElement);
        return this;
    }
}
