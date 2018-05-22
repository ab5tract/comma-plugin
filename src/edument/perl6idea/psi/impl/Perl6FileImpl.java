package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6FileStub;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.index.Perl6AllRoutinesStubIndex;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Perl6FileImpl extends PsiFileBase implements Perl6File {
    private static final Perl6Symbol[] UNIT_SYMBOLS = new Perl6Symbol[] {
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$?FILE"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$?LINE"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$?LANG"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "%?RESOURCES"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$?PACKAGE"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$=pod"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$=finish"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$_"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$/"),
        new Perl6ImplicitSymbol(Perl6SymbolKind.Variable, "$!")
    };

    public Perl6FileImpl(FileViewProvider viewProvider) {
        super(viewProvider, Perl6Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Perl6ModuleFileType.INSTANCE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getContainingFile();
    }

    @Override
    public List<Perl6PsiDeclaration> getExports() {
        // If possible, get the result from the stub, to avoid having to
        // build and walk the full PSI tree.
        Stub stub = getStub();
        if (stub instanceof Perl6FileStub)
            return ((Perl6FileStub)stub).getExports();

        // Otherwise, we need to walk the PSI tree.
        return PsiTreeUtil.findChildrenOfType(this, Perl6PsiDeclaration.class).stream()
              .filter(decl -> decl.isExported())
              .collect(Collectors.toList());
    }

    @Override
    public void contributeGlobals(Perl6SymbolCollector collector) {
        // Walk from the top of the PSI tree to find top-level, our-scoped packages.
        // Contribute those.
        Stub stub = this.getStub();
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
                    String scope = nested.getScope();
                    if (scope.equals("our") || scope.equals("unit")) {
                        String topName = nested.getTypeName();
                        if (topName != null && !topName.isEmpty()) {
                            Perl6PackageDecl psi = nested.getPsi();
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                psi, topName));
                            psi.contributeNestedPackagesWithPrefix(collector, topName + "::");
                        }
                    }
                }
                else {
                    addChildren = true;
                }
                if (addChildren)
                    for (Stub c : current.getChildrenStubs())
                        visit.add(c);
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
                    String scope = nested.getScope();
                    if (scope.equals("our") || scope.equals("unit")) {
                        String topName = nested.getName();
                        if (topName != null && !topName.isEmpty()) {
                            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant,
                                nested, topName));
                            nested.contributeNestedPackagesWithPrefix(collector, topName + "::");
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

    @Override
    public void contributeScopeSymbols(Perl6SymbolCollector collector) {
        for (Perl6Symbol symbol : UNIT_SYMBOLS) {
            collector.offerSymbol(symbol);
            if (collector.isSatisfied())
                return;
        }
        for (Perl6Symbol symbol : Perl6SdkType.getInstance().getCoreSettingSymbols(this)) {
            collector.offerSymbol(symbol);
            if (collector.isSatisfied())
                return;
        }
    }
}
