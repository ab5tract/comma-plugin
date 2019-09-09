package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6Variable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.stub.Perl6VariableDeclStub;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import org.jetbrains.annotations.Nullable;

public class ExternalPerl6VariableDecl extends Perl6ExternalPsiElement implements Perl6VariableDecl {
    private final Perl6Symbol mySymbol;

    public ExternalPerl6VariableDecl(Project project, ExternalPerl6File file, Perl6Symbol symbol) {
        myProject = project;
        myParent = file;
        mySymbol = symbol;
    }

    @Override
    public String getVariableName() {
        return null;
    }

    @Override
    public boolean hasInitializer() {
        return false;
    }

    @Nullable
    @Override
    public PsiElement getInitializer(Perl6Variable variable) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getInitializer() {
        return null;
    }

    @Override
    public void removeVariable(Perl6Variable variable) {

    }

    @Override
    public IStubElementType getElementType() {
        return null;
    }

    @Override
    public Perl6VariableDeclStub getStub() {
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

    }
}
