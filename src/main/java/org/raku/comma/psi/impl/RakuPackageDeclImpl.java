package org.raku.comma.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.comma.highlighter.RakuElementVisitor;
import org.raku.comma.pod.PodDomBuildingContext;
import org.raku.comma.pod.PodDomClassyDeclarator;
import org.raku.comma.psi.*;
import org.raku.comma.psi.stub.*;
import org.raku.comma.psi.symbols.*;
import org.raku.comma.psi.stub.index.RakuGlobalTypeStubIndex;
import org.raku.comma.psi.stub.index.RakuIndexableType;
import org.raku.comma.psi.stub.index.RakuLexicalTypeStubIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raku.comma.sdk.RakuSdkUtil;
import org.raku.comma.services.project.RakuProjectSdkService;

import java.util.*;

public class RakuPackageDeclImpl extends RakuTypeStubBasedPsi<RakuPackageDeclStub>
  implements RakuPackageDecl, PsiMetaOwner {
    private Boolean cachedTrustsOthers;

    public RakuPackageDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuPackageDeclImpl(final RakuPackageDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void setMetaClass(RakuPackageDecl metaClass) {
    }

    @Override
    public @Nullable RakuPackageDecl getMetaClass() {
        try {
            RakuSingleResolutionSymbolCollector collector =
              new RakuSingleResolutionSymbolCollector(getPackageKind(), RakuSymbolKind.TypeOrConstant);
            applyLexicalSymbolCollector(collector);
            if (collector.isSatisfied() && collector.getResult().getPsi() instanceof RakuPackageDecl) {
                return (RakuPackageDecl)collector.getResult().getPsi();
            }
        } catch (AssertionError ignored) {
            // If resolution goes out of a stub, we cannot do a lot without breaking stub rules
        }
        return null;
    }

    @Override
    public String getPackageKind() {
        RakuPackageDeclStub stub = getStub();
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
        RakuStatementList list = PsiTreeUtil.findChildOfType(this, RakuStatementList.class);
        if (list == null) return false;
        for (PsiElement child : list.getChildren()) {
            if (child.getFirstChild() instanceof RakuStubCode)
                return true;
        }
        return false;
    }

    @Nullable
    @Override
    public PsiElement getPackageKeywordNode() {
        return getDeclarator();
    }

    @Override
    public RakuParameter[] getSignature() {
        RakuRoleSignature signature = PsiTreeUtil.getChildOfType(this, RakuRoleSignature.class);
        if (signature == null)
            return new RakuParameter[0];
        return PsiTreeUtil.getChildrenOfType(signature, RakuParameter.class);
    }

    public String toString() {
        return getClass().getSimpleName() + "(Raku:PACKAGE_DECLARATION)";
    }

    @Override
    public void contributeScopeSymbols(RakuSymbolCollector collector) {
        String packageName = getPackageName();
        if (packageName == null) return;
        collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable, this, "$?PACKAGE"));
        if (collector.isSatisfied()) return;
        switch (getPackageKind()) {
            case "class":
            case "grammar":
                collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable, this, "$?CLASS"));
                break;
            case "role":
                collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.Variable, this, "$?ROLE"));
                collector.offerSymbol(new RakuImplicitSymbol(RakuSymbolKind.Variable, "$?CLASS", this));
                break;
        }
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {
        super.contributeLexicalSymbols(collector);
        contributeNestedPackagesWithPrefix(collector, getPackageName() + "::");
    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        contributeInternals(collector, symbolsAllowed);
        if (collector.isSatisfied())
            return;
        collector.decreasePriority();
        String packageName = getPackageName();
        if (packageName != null && !collector.shouldTraverse(packageName))
            return;
        contributeFromElders(collector, symbolsAllowed);

        RakuPackageDecl metaClass = getMetaClass();
        if (metaClass != null) {
            collector.decreasePriority();
            metaClass.contributeMOPSymbols(collector, symbolsAllowed);
        }
    }

    // TODO Re-instate trusts support somehow
    private List<String> getTrusts() {
        List<String> trusts = new ArrayList<>();
        RakuPackageDeclStub stub = getStub();
        if (stub != null) {
            stub.getChildrenStubs().stream()
                    .filter(s -> s instanceof RakuTypeNameStub)
                    .forEach(s -> trusts.add(((RakuTypeNameStub)s).getTypeName()));
        } else {
            RakuStatementList statementList = PsiTreeUtil.findChildOfType(this, RakuStatementList.class);
            if (statementList == null) return new ArrayList<>();
            for (PsiElement statement : statementList.getChildren()) {
                if (statement.getFirstChild() instanceof RakuTrusts)
                    trusts.add(((RakuTrusts) statement.getFirstChild()).getTypeName());
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

    private void contributeInternals(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        RakuPackageDeclStub stub = getStub();
        if (stub != null) {
            for (StubElement<?> nestedStub : stub.getChildrenStubs()) {
                if (nestedStub instanceof RakuRoutineDeclStub declStub) {
                    if (declStub.isPrivate() && !symbolsAllowed.privateMethodsVisible)
                        continue;
                    if (declStub.getRoutineKind().equals("submethod") && !symbolsAllowed.submethodsVisible)
                        continue;
                    declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                    if (collector.isSatisfied()) return;
                }
                else if (nestedStub instanceof RakuScopedDeclStub scopedVar) {
                    List<StubElement> stubsUnderScoped = scopedVar.getChildrenStubs();
                    for (StubElement<?> var : stubsUnderScoped) {
                        if (var instanceof RakuVariableDeclStub declStub) {
                            if (!declStub.getScope().equals("has"))
                                continue;
                            declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                            if (collector.isSatisfied()) return;
                        }
                    }
                } else if (nestedStub instanceof RakuRegexDeclStub declStub) {
                    declStub.getPsi().contributeMOPSymbols(collector, symbolsAllowed);
                    if (collector.isSatisfied()) return;
                }
            }
            return;
        }

        RakuStatementList list = PsiTreeUtil.findChildOfType(this, RakuStatementList.class);
        if (list == null) return;
        for (PsiElement child : list.getChildren()) {
            PsiElement firstChild = child.getFirstChild();
            if (firstChild instanceof RakuRoutineDecl decl) {
                decl.contributeMOPSymbols(collector, symbolsAllowed);
            }
            else if (firstChild instanceof RakuMultiDecl) {
                RakuRoutineDecl maybeDecl = PsiTreeUtil.getChildOfType(firstChild, RakuRoutineDecl.class);
                if (maybeDecl != null)
                    maybeDecl.contributeMOPSymbols(collector, symbolsAllowed);
            }
            else if (firstChild instanceof RakuScopedDecl decl) {
                if (decl.getScope().equals("has")) {
                    RakuVariableDecl varDecl = PsiTreeUtil.getChildOfType(decl, RakuVariableDecl.class);
                    if (varDecl != null)
                        varDecl.contributeMOPSymbols(collector, symbolsAllowed);
                }
            }
            else if (firstChild instanceof RakuRegexDecl) {
                ((RakuRegexDecl)firstChild).contributeMOPSymbols(collector, symbolsAllowed);
            }
            if (collector.isSatisfied()) return;
        }
    }

    private void contributeFromElders(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        RakuPackageDeclStub stub = getStub();
        List<Pair<String, RakuPackageDecl>> rakuPackageDecls = new ArrayList<>();
        List<Pair<String, String>> externals = new ArrayList<>();
        boolean isAny = true;
        boolean isMu = true;
        boolean isGrammar = getPackageKind().equals("grammar");

        if (stub != null) {
            List<StubElement> children = stub.getChildrenStubs();
            for (StubElement<?> child : children) {
                if (!(child instanceof RakuTraitStub traitStub)) continue;
                if (!traitStub.getTraitModifier().equals("does") && !traitStub.getTraitModifier().equals("is")) continue;
                String name = traitStub.getTraitName();
                Project project = getProject();
                List<RakuIndexableType> indexables = new ArrayList<>();
                var lexicalIndex = RakuLexicalTypeStubIndex.getInstance();
                indexables.addAll(StubIndex.getElements(lexicalIndex.getKey(),
                                                        name,
                                                        project,
                                                        GlobalSearchScope.projectScope(project),
                                                        RakuIndexableType.class));
                var globalIndex = RakuGlobalTypeStubIndex.getInstance();
                indexables.addAll(StubIndex.getElements(globalIndex.getKey(),
                                                        name,
                                                        project,
                                                        GlobalSearchScope.projectScope(project),
                                                        RakuIndexableType.class));
                if (indexables.size() == 1) {
                    RakuPackageDecl decl = (RakuPackageDecl) indexables.getFirst();
                    rakuPackageDecls.add(Pair.create(traitStub.getTraitModifier(), decl));
                } else {
                    externals.add(Pair.create(traitStub.getTraitModifier(), name));
                }
                isAny = !name.equals("Mu");
            }
        }
        else {
            for (RakuTrait trait : getTraits()) {
                if (!(trait.getTraitModifier().equals("does") || trait.getTraitModifier().equals("is"))) continue;
                PsiElement element = trait.getTraitModifier().equals("does") ?
                                     PsiTreeUtil.findChildOfType(trait, RakuTypeName.class) :
                                     PsiTreeUtil.findChildOfType(trait, RakuIsTraitName.class);
                if (element == null) continue;
                PsiReference ref = element.getReference();
                if (ref == null) continue;
                PsiElement decl = ref.resolve();
                if (decl instanceof RakuPackageDecl)
                    rakuPackageDecls.add(Pair.create(trait.getTraitModifier(), (RakuPackageDecl)decl));
                else
                    externals.add(Pair.create(trait.getTraitModifier(), trait.getTraitName()));
                if (trait.getTraitName().equals("Mu"))
                    isAny = false;
            }
        }

        // Contribute from explicit parents, either local or external
        for (Pair<String, RakuPackageDecl> pair : rakuPackageDecls) {
            // Local perl6PackageDecl
            RakuPackageDecl typeRef = pair.second;
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
        RakuFile coreSetting = getProject().getService(RakuProjectSdkService.class)
                                           .getSymbolCache()
                                           .getCoreSettingFile();
        if (coreSetting == null) return;

        MOPSymbolsAllowed allowed = new MOPSymbolsAllowed(false, false, false, getPackageKind().equals("role"));

        if (rakuPackageDecls.size() != 0 || externals.size() != 0) return;

        collector.decreasePriority();
        if (isGrammar) {
            RakuSdkUtil.contributeParentSymbolsFromCore(collector, coreSetting, "Cursor", allowed);
        }
        collector.decreasePriority();
        if (isAny) {
            RakuSdkUtil.contributeParentSymbolsFromCore(collector, coreSetting, "Any", allowed);
        }
        collector.decreasePriority();
        // Always contribute Mu
        RakuSdkUtil.contributeParentSymbolsFromCore(collector, coreSetting, "Mu", allowed);
    }

    private void contributeExternalPackage(RakuSymbolCollector collector, String typeName,
                                           MOPSymbolsAllowed symbolsAllowed) {
        RakuSingleResolutionSymbolCollector extCollector =
                new RakuSingleResolutionSymbolCollector(typeName, RakuSymbolKind.TypeOrConstant);
        applyExternalSymbolCollector(extCollector);
        RakuSymbol collectorResult = extCollector.getResult();
        if (collectorResult != null && collectorResult.getPsi() instanceof RakuPackageDecl externalPackage) {
            externalPackage.contributeMOPSymbols(collector, symbolsAllowed);
        }
    }

    @Override
    public void contributeNestedPackagesWithPrefix(RakuSymbolCollector collector, String prefix) {
        // Walk to find immediately nested packages, but not those within them
        // (we make a recursive contribute call on those).
        RakuPackageDeclStub stub = getStub();
        if (stub != null) {
            contributeNestedPackagesWithPrefixStub(collector, prefix, stub);
        }
        else {
            contributeNestedPackagesWithPrefixNonStub(collector, prefix);
        }
    }

    private void contributeNestedPackagesWithPrefixNonStub(RakuSymbolCollector collector, String prefix) {
        Queue<RakuPsiElement> visit = new ArrayDeque<>();
        visit.add(this);
        while (!visit.isEmpty()) {
            RakuPsiElement current = visit.remove();
            boolean addChildren = false;
            if (current == this) {
                addChildren = true;
            }
            else if (current instanceof RakuPackageDecl nested) {
                if (nested.getScope().equals("our")) {
                    String nestedName = nested.getPackageName();
                    if (nestedName != null && !nestedName.isEmpty()) {
                        collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.TypeOrConstant,
                                                                            nested, prefix + nestedName));
                        if (collector.isSatisfied()) return;
                        nested.contributeNestedPackagesWithPrefix(collector, prefix + nestedName + "::");
                    }
                }
            }
            else {
                addChildren = true;
            }
            if (addChildren) {
                PsiElement e = current.getFirstChild();
                while (e != null) {
                    if (e instanceof RakuPsiElement)
                        visit.add((RakuPsiElement)e);
                    e = e.getNextSibling();
                }
            }
        }
    }

    private static void contributeNestedPackagesWithPrefixStub(RakuSymbolCollector collector, String prefix, RakuPackageDeclStub stub) {
        Queue<Stub> visit = new LinkedList<>();
        visit.add(stub);
        while (!visit.isEmpty()) {
            Stub current = visit.remove();
            boolean addChildren = false;
            if (current == stub) {
                addChildren = true;
            }
            else if (current instanceof RakuPackageDeclStub nested) {
                if (nested.getScope().equals("our")) {
                    String nestedName = nested.getTypeName();
                    if (nestedName != null && !nestedName.isEmpty()) {
                        RakuPackageDecl psi = nested.getPsi();
                        collector.offerSymbol(new RakuExplicitAliasedSymbol(RakuSymbolKind.TypeOrConstant,
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
    public List<RakuPackageDecl> collectParents() {
        List<RakuPackageDecl> parents = new ArrayList<>();
        for (RakuTrait trait : getTraits()) {
            RakuIsTraitName isTrait = PsiTreeUtil.findChildOfType(trait, RakuIsTraitName.class);
            if (isTrait != null) {
                PsiReference reference = isTrait.getReference();
                assert reference != null;
                PsiElement resolved = reference.resolve();
                if (resolved instanceof RakuPackageDecl)
                    parents.add((RakuPackageDecl)resolved);
            }
            else {
                RakuTypeName doesTrait = PsiTreeUtil.findChildOfType(trait, RakuTypeName.class);
                if (doesTrait != null) {
                    PsiReference reference = doesTrait.getReference();
                    assert reference != null;
                    PsiElement resolved = reference.resolve();
                    if (resolved instanceof RakuPackageDecl)
                        parents.add((RakuPackageDecl)resolved);
                }
            }
        }
        return parents;
    }

    @Override
    public List<RakuPackageDecl> collectChildren() {
        List<RakuPackageDecl> children = new ArrayList<>();
        RakuGlobalTypeStubIndex index = RakuGlobalTypeStubIndex.getInstance();
        Project project = getProject();
        String name = getPackageName();
        Collection<String> keys = index.getAllKeys(project);
        keys.forEach(key -> {
            Collection<RakuIndexableType> psi = StubIndex.getElements(index.getKey(), key, project, GlobalSearchScope.allScope(project), RakuIndexableType.class);
            if (psi.size() == 1) {
                for (RakuIndexableType type : psi) {
                    if (!(type instanceof RakuPackageDecl)) {
                        continue;
                    }
                    RakuTrait childTrait = ((RakuPackageDecl)type).findTrait("does", name);
                    if (Objects.nonNull(childTrait)) {
                        children.add((RakuPackageDecl) type);
                    }
                    childTrait = ((RakuPackageDecl)type).findTrait("is", name);
                    if (Objects.nonNull(childTrait)) {
                        children.add((RakuPackageDecl) type);
                    }
                }
            }
        });
        return children;
    }

    @Override
    public @Nullable RakuTrait findTrait(String mod, String name) {
        RakuPackageDeclStub stub = getStub();
        if (stub == null)
            return super.findTrait(mod, name);

        List<StubElement> children = stub.getChildrenStubs();
        for (StubElement<?> child : children) {
            if (!(child instanceof RakuTraitStub traitStub))
                continue;
            if (traitStub.getTraitModifier().equals(mod) && traitStub.getTraitName().equals(name))
                return traitStub.getPsi();
        }
        return null;
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

            @Override
            public Object @NotNull [] getDependencies() {
                return ArrayUtil.EMPTY_OBJECT_ARRAY;
            }
        };
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        PsiElement nameElement = RakuElementFactory.createTypeDeclarationName(getProject(), name);
        PsiElement identifier = getNameIdentifier();
        if (identifier != null)
            identifier.replace(nameElement);
        return this;
    }

    @Override
    public void collectPodAndDocumentables(PodDomBuildingContext context) {
        String kind = getPackageKind();
        String name = getPackageName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split("::");
            String shortName = parts[parts.length - 1];
            String globalName = context.prependGlobalNameParts(name);
            String scope = getScope();
            boolean isLexical = !(scope.equals("our") || scope.equals("unit"));
            RakuTrait exportTrait = findTrait("is", "export");
            if (isLexical)
                context.enterLexicalPackage();
            else
                context.enterGlobalNamePart(name);
            boolean visible = !isLexical && globalName != null || exportTrait != null;
            if (visible && !(kind.equals("package") || kind.equals("module"))) {
                PodDomClassyDeclarator type = new PodDomClassyDeclarator(getTextOffset(), shortName, globalName,
                        getDocBlocks(), exportTrait, kind);
                context.addType(type);
                context.enterClassyType(type);
            }
            else {
                context.enterClassyType(null);
            }
            super.collectPodAndDocumentables(context);
            context.exitClassyType();
            if (isLexical)
                context.exitLexicalPackage();
            else
                context.exitGlobalNamePart();
        }
        else {
            super.collectPodAndDocumentables(context);
        }
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RakuElementVisitor) {
            ((RakuElementVisitor)visitor).visitRakuElement(this);
        } else {
            super.accept(visitor);
        }
    }
}
