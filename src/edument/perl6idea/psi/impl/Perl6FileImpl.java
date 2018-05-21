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
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiDeclaration;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.Perl6FileStub;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.index.Perl6AllRoutinesStubIndex;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.ProjectModulesStubIndex;
import edument.perl6idea.psi.symbols.Perl6ImplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
