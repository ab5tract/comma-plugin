package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExternalPerl6PackageDecl extends Perl6ExternalPsiElement implements Perl6PackageDecl {
    private final Perl6ExternalPackage mySymbol;

    public ExternalPerl6PackageDecl(Project project, Perl6File file, Perl6Symbol symbol) {
        myProject = project;
        myParent = file;

        mySymbol = (Perl6ExternalPackage)symbol;
    }

    @Override
    public String getPackageKind() {
        return mySymbol.getPackageKind() == Perl6PackageKind.CLASS ? "class" : "role";
    }

    @Override
    public String getPackageName() {
        return mySymbol.getName();
    }

    @NotNull
    @Override
    public String getName() {
        return mySymbol.getName();
    }

    @Nullable
    @Override
    public PsiElement getPackageKeywordNode() {
        return null;
    }

    @Override
    public void contributeNestedPackagesWithPrefix(Perl6SymbolCollector collector, String prefix) {

    }

    @Override
    public List<Perl6PackageDecl> collectChildren() {
        return null;
    }

    @Override
    public List<Perl6PackageDecl> collectParents() {
        return null;
    }

    @Override
    public IStubElementType getElementType() {
        return null;
    }

    @Override
    public Perl6PackageDeclStub getStub() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {

    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        for (String methodName : mySymbol.methods()) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Method, new ExternalPerl6RoutineDecl(myProject, myParent, new Perl6ExternalSymbol(Perl6SymbolKind.Method, "." + methodName))));
            if (collector.isSatisfied()) return;
        }
        for (String privateMethodName : mySymbol.privateMethods()) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Method, new ExternalPerl6RoutineDecl(myProject, myParent, new Perl6ExternalSymbol(Perl6SymbolKind.Method, privateMethodName))));
            if (collector.isSatisfied()) return;
        }
        for (String variable : mySymbol.attributes()) {
            collector.offerSymbol(new Perl6ExplicitSymbol(Perl6SymbolKind.Variable, new ExternalPerl6VariableDecl(myProject, myParent, new Perl6ExternalSymbol(Perl6SymbolKind.Variable, variable))));
            if (collector.isSatisfied()) return;
        }
    }
}
