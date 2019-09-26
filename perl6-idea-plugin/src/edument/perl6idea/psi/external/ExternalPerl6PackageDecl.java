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
import edument.perl6idea.sdk.Perl6SdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExternalPerl6PackageDecl extends Perl6ExternalPsiElement implements Perl6PackageDecl {
    private final String myType;
    private List<Perl6RoutineDecl> myRoutines = new ArrayList<>();
    private List<Perl6VariableDecl> myAttributes = new ArrayList<>();
    private String myBase;
    private String myPackageKind;
    private String myName;

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type, String base,
                                    List<Perl6RoutineDecl> routines, List<Perl6VariableDecl> attrs) {
        this(project, file, kind, name, type, base);
        myRoutines = routines;
        myAttributes = attrs;
    }

    public ExternalPerl6PackageDecl(Project project, Perl6File file, String kind, String name, String type, String base) {
        myProject = project;
        myParent = file;
        switch (kind) {
            case "ro":
                myPackageKind = "role";
                break;
            case "c":
                myPackageKind = "c";
                break;
        }
        switch (base) {
            case "M":
                myBase = "Mu";
                break;
            case "A":
                myBase = "Any";
                break;
            case "C":
                myBase = "Cool";
                break;
            default:
                myBase = base;
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
        if (myBase.isEmpty())
            return;
        Perl6File coreSetting = Perl6SdkType.getInstance().getCoreSettingFile(getProject());
        MOPSymbolsAllowed allowed = new MOPSymbolsAllowed(false, false, false, getPackageKind().equals("role"));

        // Add additional methods from Mu/Any/Cool as we don't
        // have a more complex structure for parents for now

        // If the type is Cool, but not Cool itself, we have to add Cool methods (if it is Cool itself, the methods are already there)
        if (!myName.equals("Cool") && myBase.equals("Cool")) {
            collector.decreasePriority();
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Cool", allowed);
        }
        // If the type is not Any itself and its base is either Any or Cool, add Any methods
        if (!myName.equals("Any") && !myBase.equals("Mu")) {
            collector.decreasePriority();
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Any", allowed);
        }
        // If the name is not Mu, add Mu methods
        if (!myName.equals("Mu")) {
            collector.decreasePriority();
            Perl6SdkType.contributeParentSymbolsFromCore(collector, coreSetting, "Mu", allowed);
        }
    }
}
