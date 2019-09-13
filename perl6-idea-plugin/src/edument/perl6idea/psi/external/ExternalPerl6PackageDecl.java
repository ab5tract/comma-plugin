package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.stub.Perl6PackageDeclStub;
import edument.perl6idea.psi.symbols.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExternalPerl6PackageDecl extends Perl6ExternalPsiElement implements Perl6PackageDecl {
    private final String myType;
    private List<Perl6RoutineDecl> myRoutines = new ArrayList<>();
    private List<Perl6VariableDecl> myAttributes = new ArrayList<>();
    private String myPackageKind;
    private String myName;

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type,
                                    List<Perl6RoutineDecl> routines,
                                    List<Perl6VariableDecl> attrs) {
        this(project, file, kind, name, type);
        myRoutines = routines;
        myAttributes = attrs;
    }

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type) {
        myProject = project;
        myParent = file;
        myPackageKind = kind;
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
            if (!symbolsAllowed.privateMethodsVisible && routine.getRoutineName().startsWith("!"))
                continue;
            if (!symbolsAllowed.submethodsVisible && routine.getRoutineKind().equals("submethod"))
                continue;
            routine.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
        for (Perl6VariableDecl variable : myAttributes) {
            variable.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
    }
}
