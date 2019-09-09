package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExternalPerl6RoutineDecl extends Perl6ExternalPsiElement implements Perl6RoutineDecl {
    Perl6Symbol mySymbol;

    public ExternalPerl6RoutineDecl(Project project, PsiElement parent, Perl6Symbol symbol) {
        myProject = project;
        myParent = parent;
        mySymbol = symbol;
    }

    @Override
    public String getRoutineKind() {
        return "method";
    }

    @Override
    public String getRoutineName() {
        return mySymbol.getName();
    }

    @NotNull
    @Override
    public String getName() {
        return mySymbol.getName();
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isStubbed() {
        return false;
    }

    @NotNull
    @Override
    public PsiElement[] getContent() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public Perl6Parameter[] getParams() {
        return new Perl6Parameter[0];
    }

    @Override
    public String getMultiness() {
        return null;
    }

    @Override
    public PsiElement getDeclaratorNode() {
        return null;
    }

    @Override
    public IStubElementType getElementType() {
        return null;
    }

    @Override
    public Perl6RoutineDeclStub getStub() {
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
    public String getSignature() {
        return null;
    }

    @Nullable
    @Override
    public Perl6Signature getSignatureNode() {
        return null;
    }

    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {

    }
}
