package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.index.Perl6AllRoutinesStubIndex;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.psi.stubs.StubIndex.getInstance;
import static edument.perl6idea.parsing.Perl6ElementTypes.*;

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

    @Override
    public Object[] privateMethods() {
        //Perl6PackageDeclStub stub = getStub();
        //if (stub != null)
        //    return stubBasedPrivatemethods(stub);

        List<Perl6PsiElement> result = new ArrayList<>();
        Perl6Blockoid blockoid = findChildByType(BLOCKOID);
        if (blockoid == null) return new Perl6RoutineDecl[0];
        ASTNode list = blockoid.getNode().findChildByType(STATEMENT_LIST);
        if (list == null) return new Perl6RoutineDecl[0];
        ASTNode[] statements = list.getChildren(TokenSet.create(STATEMENT));
        for (ASTNode node : statements) {
            PsiElement child = node.getFirstChildNode().getPsi();
            if (child instanceof Perl6RoutineDeclImpl) {
                Perl6RoutineDecl routine = (Perl6RoutineDecl)child;
                if (routine.isPrivateMethod())
                    result.add(routine);
            }
        }
        //List<Perl6Trait> appliedRoles = getTraits().stream().filter(t -> t.getTraitModifier().equals("does")).collect(Collectors.toList());

        //List<String> appliedRolesNames = appliedRoles.stream().map(r -> r.getTraitName()).collect(Collectors.toList());
        //Perl6GlobalTypeStubIndex typeStubIndex = Perl6GlobalTypeStubIndex.getInstance();
        //Project project = getProject();
        //Collection<String> packageKeys = typeStubIndex.getAllKeys(project).stream().filter(k -> appliedRolesNames.contains(k)).collect(Collectors.toList());

        //ArrayList<Perl6IndexableType> packageStubs = new ArrayList<>();
        //for (String packageKey : packageKeys) {
        //    packageStubs.addAll(typeStubIndex.get(packageKey, project, GlobalSearchScope.projectScope(project)));
        //}
        //for (Perl6IndexableType indexable : packageStubs) {
        //    if (indexable instanceof Perl6PackageDecl) {
        //        Perl6PackageDeclStub stub = ((Perl6PackageDecl)indexable).getStub();
        //        if (stub != null && !stub.getPackageKind().equals("role")) continue;
        //        if (stub != null) {
        //            List<StubElement> childrenStubs = stub.getChildrenStubs();
        //            for (StubElement child : childrenStubs) {
        //                if (!(child instanceof Perl6RoutineDeclStub)) continue;
        //                if (((Perl6RoutineDeclStub)child).isPrivate())
        //                    result.add(((Perl6RoutineDeclStub)child).getPsi());
        //            }
        //        } else {
        //            result.addAll(Arrays.asList(((Perl6PackageDecl)indexable).privateMethods()));
        //        }
        //    }
        //}
        return result.toArray(new Perl6RoutineDecl[result.size()]);
    }

    private Object[] stubBasedPrivatemethods(Perl6PackageDeclStub stub) {
        List<Perl6RoutineDecl> routines = new ArrayList<>();
        List<StubElement> children = stub.getChildrenStubs();
        for (StubElement child : children) {
            if (child instanceof Perl6RoutineDeclStub) {
                if (((Perl6RoutineDeclStub)child).isPrivate())
                    routines.add(((Perl6RoutineDeclStub)child).getPsi());
            }
        }

        return new Perl6RoutineDecl[0];
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
