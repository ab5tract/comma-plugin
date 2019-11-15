package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.symbols.*;
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExternalPerl6PackageDecl extends Perl6ExternalPsiElement implements Perl6PackageDecl {
    private final String myType;
    private List<Perl6RoutineDecl> myRoutines = new ArrayList<>();
    private List<Perl6VariableDecl> myAttributes = new ArrayList<>();
    private List<String> myMRO;
    private String myPackageKind;
    private String myName;
    private Set<String> myGettersPool = new HashSet<>();

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type, String base,
                                    List<Perl6RoutineDecl> routines, List<Perl6VariableDecl> attrs, List<String> mro) {
        this(project, file, kind, name, type, base);
        myMRO = mro;
        myRoutines = routines;
        myAttributes = attrs;
        for (Perl6VariableDecl decl : myAttributes) {
            String[] names = decl.getVariableNames();
            for (String getterName : names) {
                if (Perl6Variable.getTwigil(getterName) == '.')
                    myGettersPool.add(getterName.substring(2)); // cut off sigil
            }
        }
    }

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type, String base) {
        myProject = project;
        myParent = file;
        switch (kind) {
            case "ro":
                myPackageKind = "role";
                break;
            case "c":
                myPackageKind = "class";
                break;
        }
        myName = name;
        myType = type;
    }

    @Override
    public String getPackageKind() {
        return myPackageKind;
    }

    @Override
    public String getPackageName() {
        return getName();
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
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
        return new ArrayList<>();
    }

    @Override
    public List<Perl6PackageDecl> collectParents() {
        return new ArrayList<>();
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
    public String inferType() {
        return myType;
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        for (Perl6RoutineDecl routine : myRoutines) {
            String name = routine.getRoutineName();
            if (!symbolsAllowed.privateMethodsVisible && name.startsWith("!"))
                continue;
            if (!symbolsAllowed.submethodsVisible && routine.getRoutineKind().equals("submethod"))
                continue;
            if (myGettersPool.contains(name))
                continue;
            routine.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
        for (Perl6VariableDecl variable : myAttributes) {
            variable.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
        for (String mroParent : myMRO) {
            Perl6Symbol parent = resolveLexicalSymbol(Perl6SymbolKind.TypeOrConstant, mroParent);
            if (parent != null && parent.getPsi() instanceof Perl6PackageDecl) {
                Perl6PackageDecl decl = (Perl6PackageDecl)parent.getPsi();
                decl.contributeMOPSymbols(collector, symbolsAllowed);
            }
        }
    }
}